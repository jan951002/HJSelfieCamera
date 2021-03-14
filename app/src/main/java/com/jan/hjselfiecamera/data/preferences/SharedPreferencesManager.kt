package com.jan.hjselfiecamera.data.preferences

import android.content.Context

class SharedPreferencesManager(context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "hjselfiecamera_preferences"
        private const val KEY_STRING = "keyString"
        private const val KEY_INT = "keyInt"
        private const val KEY_BOOLEAN = "keyBoolean"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0)
    private val preferencesEditor = sharedPreferences.edit()

    private fun putString(key: String, value: String) {
        with(preferencesEditor) {
            putString(key, value)
            commit()
        }
    }

    private fun putInt(key: String, value: Int) {
        with(preferencesEditor) {
            putInt(key, value)
            commit()
        }
    }

    private fun putBoolean(key: String, value: Boolean) {
        with(preferencesEditor) {
            putBoolean(key, value)
            commit()
        }
    }


    var stringPreference
        get() = sharedPreferences.getString(KEY_STRING, "") ?: ""
        set(value) = putString(KEY_STRING, value)

    var intPreference
        get() = sharedPreferences.getInt(KEY_INT, 0)
        set(value) = putInt(KEY_INT, value)

    var booleanPreference
        get() = sharedPreferences.getBoolean(KEY_BOOLEAN, false)
        set(value) = putBoolean(KEY_BOOLEAN, value)
}