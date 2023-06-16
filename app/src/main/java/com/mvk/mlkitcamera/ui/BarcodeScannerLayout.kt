package com.mvk.mlkitcamera.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.mvk.mlkitcamera.utils.getCameraXProvider

@androidx.camera.core.ExperimentalGetImage
@Composable
fun BarcodeScannerLayout(
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    val cameraPreview = Preview.Builder().build()

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    val previewWidget = remember { PreviewView(context) }

    var isInitializing by remember { mutableStateOf(true) }

    suspend fun setupCameraPreview() {
        isInitializing = true
        val cameraProvider = context.getCameraXProvider()
        // freeze the camera preview
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            cameraPreview,
//            imageAnalysis,
        )
        cameraPreview.setSurfaceProvider(previewWidget.surfaceProvider)
        isInitializing = false
    }

    LaunchedEffect(key1 = configuration) {
        setupCameraPreview()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = { previewWidget },
            modifier = Modifier.fillMaxSize()
        )

        if (isInitializing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.Magenta
                )
            }
        }
    }
}
