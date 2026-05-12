package com.picker.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PickerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
