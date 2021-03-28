package com.jan.hjselfiecamera.config

open class CameraConfiguration {

    var fps = 20.0f
    var previewHeight = 0
    var isAutoFocus = true

    @Synchronized
    fun setCameraFacing(facing: Int) {
        require(!(facing != CAMERA_FACING_BACK && facing != CAMERA_FACING_FRONT)) {
            "Invalid camera: $facing"
        }
        cameraFacing = facing
    }

    companion object {
        const val CAMERA_FACING_BACK =
            android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
        const val CAMERA_FACING_FRONT =
            android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT

        @get:Synchronized
        var cameraFacing = CAMERA_FACING_BACK
            protected set
    }
}