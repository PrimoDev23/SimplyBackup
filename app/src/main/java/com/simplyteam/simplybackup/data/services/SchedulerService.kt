package com.simplyteam.simplybackup.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.work.*
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.utils.SchedulerUtil
import com.simplyteam.simplybackup.data.workers.BackupWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class SchedulerService @Inject constructor(
    @ApplicationContext private val _context: Context
) {
    fun ScheduleBackup(connection: Connection) {
        val time = SchedulerUtil.GetNextSchedule(connection.ScheduleType)

        val constraintsBuilder = Constraints.Builder()

        if (connection.WifiOnly) {
            constraintsBuilder.setRequiredNetworkType(NetworkType.UNMETERED)
        } else {
            constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val inputData = Data.Builder()
            .putLong("ConnectionId", connection.Id)
            .build()

        val duration = Duration.between(LocalDateTime.now(), time)

        val workRequest = OneTimeWorkRequest.Builder(BackupWorker::class.java)
            .setConstraints(constraintsBuilder.build())
            .setInitialDelay(duration)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(_context).enqueueUniqueWork(
            "${connection.Name}-${connection.Id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Timber.d("Queued job ${connection.Name} for $time in $duration")
    }

    fun RunBackup(connection: Connection) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putLong("ConnectionId", connection.Id)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(BackupWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(_context).enqueueUniqueWork(
            "${connection.Name}-${connection.Id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun CancelBackup(connection: Connection) {
        WorkManager.getInstance(_context).cancelUniqueWork("${connection.Name}-${connection.Id}")

        Timber.d("Job ${connection.Name} canceled!")
    }

}