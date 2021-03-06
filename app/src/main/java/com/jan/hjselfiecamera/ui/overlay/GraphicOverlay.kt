package com.jan.hjselfiecamera.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.jan.hjselfiecamera.config.CameraConfiguration

class GraphicOverlay(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    private val lock = Any()
    private var previewWidth = 0
    private var previewHeight = 0
    var widthScaleValue = 1.0f
        private set
    var heightScaleValue = 1.0f
        private set
    var cameraFacing = CameraConfiguration.CAMERA_FACING_FRONT
        private set
    private val graphics: MutableList<BaseGraphic> = ArrayList()

    fun setCameraInfo(width: Int, height: Int, facing: Int) {
        synchronized(lock) {
            previewWidth = width
            previewHeight = height
            cameraFacing = facing
        }
        this.postInvalidate()
    }

    fun addGraphics(graphic: BaseGraphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        this.postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleValue = width.toFloat() / previewWidth.toFloat()
                heightScaleValue = height.toFloat() / previewHeight.toFloat()
            }
            graphics.forEach {
                it.draw(canvas)
            }
        }
    }
}