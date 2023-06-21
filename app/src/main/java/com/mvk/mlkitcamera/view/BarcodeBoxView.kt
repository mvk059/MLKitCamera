package com.mvk.mlkitcamera.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mvk.mlkitcamera.ScanningResult

class BarcodeBoxView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint()
    private val paintBarcode = Paint()
    private val paintBarcodeBoundary = Paint()
    private var scanningRect = RectF()
    private val cameraBoundaryPath = Path()
    private val scanningBoundaryPath = Path()
    private val barcodeBoundaryPath = Path()
    private val grayAreaPath = Path()
    private var scanningResult: ScanningResult? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL
        paint.color = 0x0000000
        paint.strokeWidth = 10f
        paint.alpha = 100

        paintBarcode.style = Paint.Style.STROKE
        paintBarcode.color = Color.GRAY
        paintBarcode.strokeWidth = 10f
        paintBarcode.alpha = 255

        paintBarcodeBoundary.style = Paint.Style.STROKE
        paintBarcodeBoundary.color = Color.CYAN
        paintBarcodeBoundary.strokeWidth = 10f
        paintBarcodeBoundary.alpha = 255

        scanningBoundaryPath.addRect(
            scanningRect.left,
            scanningRect.top,
            scanningRect.right,
            scanningRect.bottom,
            Path.Direction.CW
        )

        cameraBoundaryPath.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        grayAreaPath.op(cameraBoundaryPath, scanningBoundaryPath, Path.Op.XOR)
        canvas?.drawPath(grayAreaPath, paint)
        canvas?.drawRect(scanningRect, paintBarcode)

        // draw the bar code result boundary
        if (scanningResult?.barCodeResult != null) {
            scanningResult?.barCodeResult?.globalPosition?.let {
                barcodeBoundaryPath.reset()
                barcodeBoundaryPath.moveTo(it.right - it.width() * 0.2F, it.top)
                barcodeBoundaryPath.lineTo(it.right, it.top)

                barcodeBoundaryPath.lineTo(it.right, it.bottom)
                barcodeBoundaryPath.lineTo(it.right - it.width() * 0.2F, it.bottom)

                barcodeBoundaryPath.moveTo(it.left + it.width() * 0.2F, it.bottom)
                barcodeBoundaryPath.lineTo(it.left, it.bottom)
                barcodeBoundaryPath.lineTo(it.left, it.top)
                barcodeBoundaryPath.lineTo(it.left + it.width() * 0.2F, it.top)

                canvas?.drawPath(barcodeBoundaryPath, paintBarcodeBoundary)
//                drawPath(
//                    path = barcodeBoundaryPath,
//                    color = Purple700,
//                    style = Stroke(
//                        join = StrokeJoin.Bevel,
//                        width = barcodeResultBoundaryStrokeSize,
//                    ),
//                )
            }
        } else {
//            barcodeBoundaryPath.reset()
        }


    }

    fun setScanningArea(rect: RectF) {
        scanningRect = rect
        invalidate()
        requestLayout()
    }

    fun setScanningResult(scanningResult: ScanningResult?) {
        if (scanningResult != null) {
            this.scanningResult = scanningResult
        }
        invalidate()
        requestLayout()
    }

    fun reset() {
        barcodeBoundaryPath.reset()
        paintBarcodeBoundary.reset()
        invalidate()
        requestLayout()
    }
}