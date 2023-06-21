package com.mvk.mlkitcamera.view

import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mvk.mlkitcamera.BarcodeImageAnalyzer
import com.mvk.mlkitcamera.ScanningResult
import com.mvk.mlkitcamera.SingleLiveEvent
import com.mvk.mlkitcamera.di.DependencyProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class CameraViewModel : ViewModel() {

    private val barcodeResultBoundaryAnalyzer = DependencyProvider.provideBarcodeResultBoundaryAnalyzer()
    private val barcodeScanner = DependencyProvider.provideBarcodeScanner()
    internal val barcodeImageAnalyzer = DependencyProvider.provideBarcodeImageAnalyzer(barcodeScanner)

    private var isResultBottomSheetShowing = false
    private var loadingProductCode: String = ""

    private val _scanningResult = MutableLiveData<ScanningResult>()
    val scanningResult: LiveData<ScanningResult> = _scanningResult

    private val _freezeCameraPreview = SingleLiveEvent<Boolean>()
    val freezeCameraPreview: LiveData<Boolean> = _freezeCameraPreview

    init {
        setupImageAnalyzer()
    }

    fun onCameraBoundaryReady(cameraBoundary: RectF) {
        barcodeResultBoundaryAnalyzer.onCameraBoundaryReady(cameraBoundary)
    }

    fun onBarcodeScanningAreaReady(scanningArea: RectF) {
        barcodeResultBoundaryAnalyzer.onBarcodeScanningAreaReady(scanningArea)
    }

    fun calculateScanningRect(size: Int, centerPoint: PointF): RectF {
        val scanningAreaSize = size * 0.8F
        val left = centerPoint.x - scanningAreaSize * 0.5F
        val top = centerPoint.y - scanningAreaSize * 0.5F
        val right = centerPoint.x + scanningAreaSize * 0.5F
        val bottom = centerPoint.y + scanningAreaSize * 0.1F
        return RectF(left, top, right, bottom)
    }

    private fun setupImageAnalyzer() {
        barcodeImageAnalyzer.setProcessListener(
            listener = object : BarcodeImageAnalyzer.ProcessListenerAdapter() {
                override fun onSucceed(results: List<Barcode>, inputImage: InputImage) {
                    super.onSucceed(results, inputImage)
                    if (results.isNotEmpty()) {
                        handleBarcodeResults(results, inputImage)
                    }
                }
            }
        )
    }

    private fun handleBarcodeResults(results: List<Barcode>, inputImage: InputImage) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, exception ->
//                notifyErrorResult(exception)
                Log.d("SCANN", "Exception: $exception")
            }
        ) {
            if (isResultBottomSheetShowing) {
                // skip the analyzer process if the result bottom sheet is showing
                return@launch
            }

            val scanningResult = barcodeResultBoundaryAnalyzer.analyze(results, inputImage)
            _scanningResult.value = scanningResult
            if (scanningResult is ScanningResult.PerfectMatch) {
                Log.d("SCANN", "Scanning Result: ${scanningResult.barCodeResult}")
//                loadProductDetailsWithBarcodeResult(scanningResult)
                freezeCameraPreview(true)
//                delay(2000)
//                freezeCameraPreview(false)
            }
        }
    }

    private suspend fun loadProductDetailsWithBarcodeResult(scanningResult: ScanningResult.PerfectMatch) {
        val productCode = scanningResult.barCodeResult.barCode.displayValue
        if (productCode != null) {
            loadingProductCode = productCode
//            showBottomSheetLoading(scanningResult.barCodeResult)
            freezeCameraPreview(true)
            // mock API call to fetch information with barcode result
            delay(2000)
//            bindBottomSheetResultInformation(barcodeResult = scanningResult.barCodeResult)
        } else {
            // Show Error Information
        }
    }

     fun freezeCameraPreview(freeze: Boolean) {
        // true - freeze the camera preview,
        // false - resume the camera preview
        _freezeCameraPreview.value = freeze
    }

}

