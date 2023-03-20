package org.sopt.official.example

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.sopt.official.playground.auth.utils.PlaygroundLog

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PlaygroundLog.init(applicationInfo)
    }
}