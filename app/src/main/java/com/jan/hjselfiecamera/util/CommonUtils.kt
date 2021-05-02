package com.jan.hjselfiecamera.util

import android.content.Context
import android.content.res.Configuration

object CommonUtils {

    fun Context.dpToPx(dipValue: Float) =
        dipValue * resources.displayMetrics.density + 0.5f

    fun Context.isPortraitOrientation() =
        Configuration.ORIENTATION_PORTRAIT == resources.configuration.orientation
}