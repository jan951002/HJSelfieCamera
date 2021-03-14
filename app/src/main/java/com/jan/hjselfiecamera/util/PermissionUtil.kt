package com.jan.hjselfiecamera.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean =
    (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)

fun Context.requiredPermissions(): Array<String?> = try {
    val info = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    val permissions = info.requestedPermissions
    permissions ?: arrayOfNulls(0)
} catch (e: RuntimeException) {
    throw e
} catch (e: Exception) {
    arrayOfNulls(0)
}

fun Context.allPermissionsGranted(): Boolean {
    requiredPermissions().forEach { permission ->
        permission?.let { if (!isPermissionGranted(it)) return false }
    }
    return true
}

fun Activity.runtimePermissions(requestCode: Int) {
    val allPermissions: MutableList<String?> = ArrayList()
    requiredPermissions().forEach { permission ->
        permission?.let {
            if (!isPermissionGranted(it)) {
                allPermissions.add(it)
            }
        }
    }
    if (allPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(this, allPermissions.toTypedArray(), requestCode)
    }
}