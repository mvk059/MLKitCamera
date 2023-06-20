package com.mvk.mlkitcamera

import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner

class BarcodeScanningViewModel(
    private val barcodeScanner: BarcodeScanner,
) : ViewModel() {
}