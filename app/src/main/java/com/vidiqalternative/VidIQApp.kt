package com.vidiqalternative

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VidIQApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
