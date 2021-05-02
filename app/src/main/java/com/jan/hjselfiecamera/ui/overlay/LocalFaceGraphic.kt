package com.jan.hjselfiecamera.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.face.MLFaceShape
import com.jan.hjselfiecamera.R
import com.jan.hjselfiecamera.util.CommonUtils.dpToPx

class LocalFaceGraphic(
    overlay: GraphicOverlay, @field:Volatile private var face: MLFace?, private val context: Context
) : BaseGraphic(overlay) {

    private val facePain: Paint

    init {
        val lineWidth = context.dpToPx(1f)
        facePain = Paint()
        facePain.color = ContextCompat.getColor(context, R.color.purple_500)
        facePain.style = Paint.Style.STROKE
        facePain.strokeWidth = lineWidth
    }

    override fun draw(canvas: Canvas?) {
        face?.let { face ->
            val faceShape = face.getFaceShape(MLFaceShape.TYPE_FACE)
            val points = faceShape.points
            var verticalMin = Float.MAX_VALUE
            var verticalMax = 0f
            var horizontalMin = Float.MAX_VALUE
            var horizontalMax = 0f
            for (i in points.indices) {
                val point = points[i] ?: continue
                if (point.x != null && point.y != null) {
                    if (point.x > horizontalMax) horizontalMax = point.x
                    if (point.x < horizontalMin) horizontalMin = point.x
                    if (point.y > verticalMax) verticalMax = point.y
                    if (point.y < verticalMin) verticalMin = point.y
                }
            }
            val rect = Rect(
                translateX(horizontalMin).toInt(),
                translateY(verticalMin).toInt(),
                translateX(horizontalMax).toInt(),
                translateY(verticalMax).toInt()

            )
            canvas?.drawRect(rect, facePain)
        }
    }
}