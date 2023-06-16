package com.mvk.mlkitcamera.utils

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraXProvider(timeOutLong: Long = 5000): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { listenableFuture ->
        listenableFuture.addListener(
            { continuation.resume(listenableFuture.get(timeOutLong, TimeUnit.MILLISECONDS)) },
            ContextCompat.getMainExecutor(this)
        )
    }

}