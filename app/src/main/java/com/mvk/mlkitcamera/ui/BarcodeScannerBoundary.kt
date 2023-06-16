package com.mvk.mlkitcamera.ui

import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mvk.mlkitcamera.utils.dp2Px

@Composable
fun BarcodeScannerBoundary(
    modifier: Modifier = Modifier,
    width: Int,
    height: Int,
) {

    fun calculateScanningAreaRec(size: Int, centerPoint: PointF): RectF {
        val scanningAreaSize = size * 0.8F
        val left = centerPoint.x - scanningAreaSize * 0.5F
        val right = centerPoint.x + scanningAreaSize * 0.5F
        val top = centerPoint.y - scanningAreaSize * 0.3F
        val bottom = centerPoint.y + scanningAreaSize * 0.3F
        return RectF(left, top, right, bottom)
    }

    val scanningAreaPath: Path by remember { mutableStateOf(Path()) }
    val cameraBoundaryPath: Path by remember { mutableStateOf(Path()) }

    val centerPoint = PointF(width * 0.5F, height * 0.5F)
    val scanningAreaRect = calculateScanningAreaRec(size = minOf(width, height), centerPoint)
    val scanningFrameStrokeSize = dp2Px(dp = 4.dp)
    val scanningFrameCornerRadius = dp2Px(dp = 4.dp)

    Canvas(
        modifier = modifier.fillMaxSize(),
        onDraw = {
            scanningAreaPath.reset()
            cameraBoundaryPath.reset()

            // Draw scanning area border
            scanningAreaPath.addRect(
                Rect(
                    left = scanningAreaRect.left,
                    top = scanningAreaRect.top,
                    right = scanningAreaRect.right,
                    bottom = scanningAreaRect.bottom,
                )
            )
            cameraBoundaryPath.addRect(Rect(Offset.Zero, Offset(width.toFloat(), height.toFloat())))
            drawPath(
                path = Path.combine(
                    operation = PathOperation.Xor,
                    scanningAreaPath,
                    cameraBoundaryPath
                ),
                color = Color.Black,
                alpha = 0.5f,
            )

            // draw the scanning area frame
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(x = scanningAreaRect.left, y = scanningAreaRect.top),
                size = Size(width = scanningAreaRect.width(), height = scanningAreaRect.height()),
                alpha = 1F,
                style = Stroke(
                    join = StrokeJoin.Round,
                    width = scanningFrameStrokeSize
                ),
                cornerRadius = CornerRadius(
                    x = scanningFrameCornerRadius,
                    y = scanningFrameCornerRadius
                )
            )
        }
    )
}