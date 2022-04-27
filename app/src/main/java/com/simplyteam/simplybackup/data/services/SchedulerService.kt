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
import javax.inject.Inject

class SchedulerService @Inject constructor(
    @ApplicationContext private val _context: Context
) {

    private val _workManager = WorkManager.getInstance(_context)

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

        val workRequest = OneTimeWorkRequest.Builder(BackupWorker::class.java)
            .setConstraints(constraintsBuilder.build())
            .setInitialDelay(Duration.ofMillis(time.timeInMillis - System.currentTimeMillis()))
            .setInputData(inputData)
            .build()

        _workManager.enqueueUniqueWork(
            "${connection.Name}-${connection.Id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Timber.d("Queued job ${connection.Name} for ${time.time}")
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

        _workManager.enqueueUniqueWork(
            "${connection.Name}-${connection.Id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun CancelBackup(connection: Connection) {
        _workManager.cancelUniqueWork("${connection.Name}-${connection.Id}")

        Timber.d("Job ${connection.Name} canceled!")
    }

}