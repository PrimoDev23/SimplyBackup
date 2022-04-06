package com.simplyteam.simplybackup.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var SchedulerService: SchedulerService

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let { context ->
            GlobalScope.launch {
                val connections = ConnectionRepository.GetAllConnections(context)

                for (connection in connections) {
                    SchedulerService.ScheduleBackup(context, connection)
                }
            }
        }
    }
}