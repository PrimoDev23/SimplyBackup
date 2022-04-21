package com.simplyteam.simplybackup.common

import android.app.Application
import com.simplyteam.simplybackup.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SimplyBackupApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }else{
            Timber.plant(ReleaseTree())
        }
    }

}