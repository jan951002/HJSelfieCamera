package com.jan.hjselfiecamera.ui.camera

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.huawei.hms.mlsdk.common.LensEngine
import com.jan.hjselfiecamera.ui.overlay.GraphicOverlay
import java.io.IOException

class LensEnginePreview(context: Context, attributeSet: AttributeSet?) :
    ViewGroup(context, attributeSet) {

    private val mContext = context
    private val mSurfaceView = SurfaceView(context)
    private var mStartRequested = false
    private var mSurfaceAvailable = false
    private var mLensEngine: LensEngine? = null
    private var mOverlay: GraphicOverlay? = null

    init {
        addView(mSurfaceView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

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
        } ?: run {
            stop()
        }
    }

    private fun stop() = mLensEngine?.close()

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
                if (Configuration.ORIENTATION_PORTRAIT == mContext.resources.configuration.orientation) {
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