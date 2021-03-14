package com.jan.hjselfiecamera.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity class
 * @author Jaime Trujillo
 */
abstract class BaseActivity : AppCompatActivity() {

    @LayoutRes
    protected abstract fun layoutRes(): Int

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())
    }
}