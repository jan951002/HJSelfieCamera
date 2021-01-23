package com.jan.hjselfiecamera.ui.main

import android.content.Intent
import android.os.Bundle
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.jan.hjselfiecamera.R
import com.jan.hjselfiecamera.base.BaseActivity
import com.jan.hjselfiecamera.ui.login.AuthActivity
import com.jan.hjselfiecamera.util.showToastLengthShort
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun layoutRes(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnLogout.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        logoutWithHuaweiId()
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