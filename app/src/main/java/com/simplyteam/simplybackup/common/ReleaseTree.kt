package com.simplyteam.simplybackup.common

import android.annotation.SuppressLint
import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    @SuppressLint("LogNotTimber")
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if(priority == Log.ERROR){
            Log.e(tag, message)
        }
    }

}