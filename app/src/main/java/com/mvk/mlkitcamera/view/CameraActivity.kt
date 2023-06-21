package com.mvk.mlkitcamera.view

import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mvk.mlkitcamera.R


@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class CameraActivity : AppCompatActivity() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var previewView: PreviewView
    private lateinit var barcodeBoxView: BarcodeBoxView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        previewView = findViewById(R.id.previewView)
        barcodeBoxView = findViewById(R.id.barcodeBoxView)

        setupObservers()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        startCamera() {

            viewModel.onCameraBoundaryReady(previewView.toRectF())
            viewModel.onBarcodeScanningAreaReady(previewView.toRectF())
            val scanningAreaRect = viewModel.calculateScanningRect(
                minOf(previewView.width, previewView.height),
                PointF(previewView.width * 0.5f, previewView.height * 0.5f)
            )
            viewModel.onBarcodeScanningAreaReady(scanningAreaRect)
            barcodeBoxView.setScanningArea(scanningAreaRect)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setupObservers() {
        viewModel.scanningResult.observe(this) {
//            if (viewModel.freezeCameraPreview.value == true)
            barcodeBoxView.setScanningResult(it)
        }

        viewModel.freezeCameraPreview.observe(this) {
            if (it) {
                showDialog()
                startScanning(false)
            }
        }
    }

    private fun startCamera(onDone: () -> Unit = {}) {
        barcodeBoxView.reset()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val builder = ImageAnalysis.Builder()
                builder.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                imageAnalysis = builder.build()
                imageAnalysis.setAnalyzer(
                    viewModel.barcodeImageAnalyzer.getAnalyzerExecutor(),
                    viewModel.barcodeImageAnalyzer
                )
                startScanning(true)
                onDone()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Closing application")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.freezeCameraPreview(false)
                startCamera()
            }
            .show()
    }

    private fun View.toRectF(): RectF {
        return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    private fun startScanning(scan: Boolean) {
        try {
            cameraProvider.unbindAll()
            if (scan) cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (e: Exception) {
            Log.d("Scann", e.stackTraceToString())
        }
    }
}