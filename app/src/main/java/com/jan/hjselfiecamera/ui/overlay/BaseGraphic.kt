package com.jan.hjselfiecamera.ui.overlay

import android.graphics.Canvas
import com.huawei.hms.mlsdk.common.LensEngine

abstract class BaseGraphic(private val graphicOverlay: GraphicOverlay) {

    abstract fun draw(canvas: Canvas?)

    fun scaleX(x: Float) = x * graphicOverlay.widthScaleValue

    fun scaleY(y: Float) = y * graphicOverlay.heightScaleValue

    fun translateX(x: Float) = if (graphicOverlay.cameraFacing == LensEngine.FRONT_LENS) {
        graphicOverlay.width - scaleX(x)
    } else {
        scaleX(x)
    }

    fun translateY(y: Float) = scaleY(y)
}