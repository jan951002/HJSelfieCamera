package com.jan.hjselfiecamera.ui.camera

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.huawei.hms.mlsdk.common.LensEngine
import com.jan.hjselfiecamera.ui.overlay.GraphicOverlay
import com.jan.hjselfiecamera.util.CommonUtils.isPortraitOrientation
import java.io.IOException

class LensEnginePreview(context: Context, attributeSet: AttributeSet?) :
    ViewGroup(context, attributeSet) {

    private val mSurfaceView = SurfaceView(context)
    private var mStartRequested = false
    private var mSurfaceAvailable = false
    private var mLensEngine: LensEngine? = null
    private var mOverlay: GraphicOverlay? = null

    init {
        mSurfaceView.holder.addCallback(SurfaceCallback())
        addView(mSurfaceView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = 320
        var previewHeight = 240
        mLensEngine?.let { lensEngine ->
            val size = lensEngine.displayDimension
            size?.let {
                previewWidth = size.width
                previewHeight = size.height
            }
        }
        if (context.isPortraitOrientation()) {
            val tempWidth = previewWidth
            previewWidth = previewHeight
            previewHeight = tempWidth
        }
        val viewWidth = right - left
        val viewHeight = bottom - top
        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()
        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * heightRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }
        for (i in 0 until this.childCount) {
            getChildAt(i).layout(
                -1 * childXOffset, -1 * childYOffset,
                childWidth - childXOffset, childHeight - childYOffset
            )
        }
        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(
                LensEnginePreview::class.java.name, "ERROR ON INIT CAMERA ${e.localizedMessage}"
            )
        }
    }

    @Throws(IOException::class)
    fun start(lensEngine: LensEngine?, overlay: GraphicOverlay) {
        mOverlay = overlay
        start(lensEngine)
    }

    @Throws(IOException::class)
    fun start(lensEngine: LensEngine?) {
        lensEngine?.let {
            mLensEngine = it
            mStartRequested = true
            startIfReady()
        } ?: run { stop() }
    }

    fun stop() = mLensEngine?.close()

    fun release() {
        mLensEngine?.release()
        mLensEngine = null
    }

    @Throws(IOException::class)
    fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mLensEngine?.run(mSurfaceView.holder)
            mOverlay?.let {
                val size = mLensEngine?.displayDimension
                val min = size?.width?.coerceAtMost(size.height) ?: 0
                val max = size?.width?.coerceAtLeast(size.height) ?: 0
                if (context.isPortraitOrientation()) {
                    it.setCameraInfo(min, max, mLensEngine?.lensType ?: 0)
                } else {
                    it.setCameraInfo(max, min, mLensEngine?.lensType ?: 0)
                }
                it.clear()
                mStartRequested = false
            }
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(
                    LensEnginePreview::class.java.name, "ERROR ON INIT CAMERA ${e.localizedMessage}"
                )
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mSurfaceAvailable = false
        }
    }
}