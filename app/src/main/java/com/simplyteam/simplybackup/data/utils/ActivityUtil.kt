package com.simplyteam.simplybackup.data.utils

import android.content.Intent
import androidx.activity.ComponentActivity
import com.simplyteam.simplybackup.R

object ActivityUtil {

    fun ComponentActivity.StartActivityWithAnimation(intent: Intent){
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun ComponentActivity.FinishActivityWithAnimation(){
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

}