package com.simplyteam.simplybackup.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.services.SchedulerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RunBackupReceiver: BroadcastReceiver() {

    @Inject
    lateinit var SchedulerService: SchedulerService

    override fun onReceive(
        p0: Context?,
        p1: Intent?
    ) {
        p0?.let { context ->
            p1?.let { intent ->
                intent.getBundleExtra("Bundle")?.let { bundle ->
                    (bundle.get("Connection") as Connection?)?.let { connection ->
                        SchedulerService.RunBackup(connection)
                    }
                }
            }
        }
    }
}