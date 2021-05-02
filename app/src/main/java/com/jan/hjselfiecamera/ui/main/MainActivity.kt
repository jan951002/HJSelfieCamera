package com.jan.hjselfiecamera.ui.main

import android.content.Intent
import android.os.Bundle
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.jan.hjselfiecamera.R
import com.jan.hjselfiecamera.base.BaseActivity
import com.jan.hjselfiecamera.ui.face.LiveFaceCameraActivity
import com.jan.hjselfiecamera.ui.face.LiveFaceCameraActivity.Companion.DETECT_MODE_INTENT_TAG
import com.jan.hjselfiecamera.ui.login.AuthActivity
import com.jan.hjselfiecamera.util.allPermissionsGranted
import com.jan.hjselfiecamera.util.runtimePermissions
import com.jan.hjselfiecamera.util.showToastLengthShort
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val PERMISSION_REQUEST = 102
    }

    override fun layoutRes(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted())
            runtimePermissions(PERMISSION_REQUEST)
        btnLogout.setOnClickListener { onBackPressed() }
        mostPeopleButton.setOnClickListener {
            val intent = Intent(this, LiveFaceCameraActivity::class.java)
            intent.putExtra(DETECT_MODE_INTENT_TAG, LiveFaceCameraActivity.DETECT_MODE_MOST_PEOPLE)
            startActivity(intent)
        }
        nearestPeopleButton.setOnClickListener {
            val intent = Intent(this, LiveFaceCameraActivity::class.java)
            intent.putExtra(
                DETECT_MODE_INTENT_TAG, LiveFaceCameraActivity.DETECT_MODE_NEAREST_PEOPLE
            )
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        logoutWithHuaweiId()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {

        }
    }

    private fun logoutWithHuaweiId() {
        val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .createParams()
        val authManager = HuaweiIdAuthManager.getService(this, authParams)
        val logoutTask = authManager.signOut()
        logoutTask.addOnSuccessListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }.addOnFailureListener {
            showToastLengthShort(getString(R.string.error_on_logout))
        }
    }
}