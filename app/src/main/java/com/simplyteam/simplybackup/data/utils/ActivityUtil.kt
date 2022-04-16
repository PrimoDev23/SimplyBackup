package com.simplyteam.simplybackup.data.utils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.activities.ConnectionConfigurationActivity

object ActivityUtil {

    fun StartConfigurationActivity(
        activity: ComponentActivity,
        connection: Connection?
    ) {
        val intent = Intent(
            activity,
            ConnectionConfigurationActivity::class.java
        )

        if (connection != null) {
            intent.putExtra(
                "Connection",
                connection
            )
        }

        activity.StartActivityWithAnimation(
            intent
        )
    }

    fun ComponentActivity.StartActivityWithAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        )
    }

    fun ComponentActivity.FinishActivityWithAnimation() {
        finish()
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_right
        )
    }

}