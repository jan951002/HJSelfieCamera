package com.jan.hjselfiecamera.base

import android.app.Application
import com.jan.hjselfiecamera.di.preferencesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class
 * @author Jaime Trujillo
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        /* Adding Koin modules to our application */
        startKoin {
            androidContext(this@BaseApplication)
            modules(preferencesModule)
        }
    }
}