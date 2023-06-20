package com.mvk.mlkitcamera.view

import android.graphics.PointF
import android.graphics.RectF
import androidx.lifecycle.ViewModel
import com.mvk.mlkitcamera.di.DependencyProvider

class CameraViewModel : ViewModel() {

    private val barcodeResultBoundaryAnalyzer = DependencyProvider.provideBarcodeResultBoundaryAnalyzer()
    private val barcodeScanner = DependencyProvider.provideBarcodeScanner()
    internal val barcodeImageAnalyzer = DependencyProvider.provideBarcodeImageAnalyzer(barcodeScanner)

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

}

