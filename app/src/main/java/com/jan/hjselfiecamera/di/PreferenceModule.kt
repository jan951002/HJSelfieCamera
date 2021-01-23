package com.jan.hjselfiecamera.di

import com.jan.hjselfiecamera.data.preferences.SharedPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Module to configure preferences dependency injection
 * @author Jaime Trujillo
 */
val preferencesModule = module {

    /* Define a factory (create a new instance each time) for SharedPreferencesManager */
    factory { SharedPreferencesManager(androidContext()) }
}