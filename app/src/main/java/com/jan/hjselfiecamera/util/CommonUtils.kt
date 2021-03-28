package com.jan.hjselfiecamera.util

import android.content.Context

object CommonUtils {

    fun dpToPx(context: Context, dipValue: Float) =
        dipValue * context.resources.displayMetrics.density + 0.5f


}