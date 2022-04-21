package com.simplyteam.simplybackup.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.receiver.BackupReceiver
import com.simplyteam.simplybackup.data.utils.SchedulerUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class SchedulerService @Inject constructor(
    @ApplicationContext private val _context: Context
) {

    fun ScheduleBackup(connection: Connection){
        val alarmManager = _context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = SchedulerUtil.GetNextSchedule(connection.ScheduleType)

        val pendingIntent = CreatePendingIntent(connection)

        alarmManager.set(
            AlarmManager.RTC,
            time.timeInMillis,
            pendingIntent
        )

        Timber.d("Queued job ${connection.Name} for ${time.time}")
    }

    fun CancelBackup(connection: Connection){
        val alarmManager = _context.getSystemService(AlarmManager::class.java)

        val pendingIntent = CreatePendingIntent(connection)

        alarmManager.cancel(pendingIntent)

        Timber.d("Job ${connection.Name} canceled!")
    }

    private fun CreatePendingIntent(connection: Connection): PendingIntent{
        val intent = Intent(_context, BackupReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("Connection", connection)
        intent.putExtra("Bundle", bundle)

        return PendingIntent.getBroadcast(
            _context,
            connection.Id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_CANCEL_CURRENT
            }
        )
    }

}