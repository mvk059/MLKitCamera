package com.mvk.mlkitcamera

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mvk.mlkitcamera.ui.BarcodeScannerLayout
import com.mvk.mlkitcamera.ui.theme.MLKitCameraTheme

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@androidx.camera.core.ExperimentalGetImage
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BarcodeScanner()
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    @Composable
    fun BarcodeScanner() {

        val cameraPermissionState = rememberPermissionState(
            Manifest.permission.CAMERA,
            onPermissionResult = { } // TODO
        )

        LaunchedEffect(
            key1 = LocalLifecycleOwner.current,
            block = {
                val status = cameraPermissionState.status
                if (!status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        )

        MLKitCameraTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        title = { Text("Barcode Scanner") },
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        content = {
                            when (cameraPermissionState.status) {
                                is PermissionStatus.Granted -> {
                                    BarcodeScannerLayout()
                                }

                                is PermissionStatus.Denied -> {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            modifier = Modifier.align(Alignment.Center),
                                            text = stringResource(id = R.string.camera_permission_is_denied),
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    @Preview(
        name = "BarcodeScanner",
        showBackground = true,
    )
    @androidx.camera.core.ExperimentalGetImage
    @Composable
    private fun BarcodeScannerPreview() {
        MLKitCameraTheme {
            BarcodeScanner()
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MLKitCameraTheme {
        Greeting("Android")
    }
}