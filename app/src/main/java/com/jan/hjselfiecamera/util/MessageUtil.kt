package com.jan.hjselfiecamera.util

import android.content.Context
import android.widget.Toast

fun Context.showToastLengthShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}