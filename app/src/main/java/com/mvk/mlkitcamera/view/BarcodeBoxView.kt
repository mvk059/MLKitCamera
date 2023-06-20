package com.mvk.mlkitcamera.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

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
    private var scanningRect = RectF()
    private val cameraBoundaryPath = Path()
    private val scanningBoundaryPath = Path()
    private val grayAreaPath = Path()

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
    }

    fun setScanningArea(rect: RectF) {
        scanningRect = rect
        invalidate()
        requestLayout()
    }
}