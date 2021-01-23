package com.jan.hjselfiecamera.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.jan.hjselfiecamera.R
import com.jan.hjselfiecamera.base.BaseActivity
import com.jan.hjselfiecamera.ui.main.MainActivity
import com.jan.hjselfiecamera.util.showToastLengthShort
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BaseActivity() {

    companion object {
        private const val RC_AUTH_HUAWEI = 1000
    }

    override fun layoutRes(): Int = R.layout.activity_auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnLogin.setOnClickListener { loginWithHuaweiIdAuth() }
    }

    private fun loginWithHuaweiIdAuth() {
        val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .setAccessToken()
            .setProfile()
            .setIdToken()
            .setUid()
            .setId()
            .createParams()
        val authManager = HuaweiIdAuthManager.getService(this, authParams)
        startActivityForResult(authManager.signInIntent, RC_AUTH_HUAWEI)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RC_AUTH_HUAWEI) {
            if (resultCode == RESULT_CANCELED)
                showToastLengthShort(getString(R.string.canceled))
            else if (resultCode == RESULT_OK) {
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToastLengthShort(getString(R.string.error_on_login))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}