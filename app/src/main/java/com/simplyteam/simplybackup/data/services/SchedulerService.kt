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
import timber.log.Timber
import java.time.LocalDate
import java.util.*

class SchedulerService {

    private val _calendarInstance = Calendar.getInstance()

    fun ScheduleBackup(context: Context, connection: Connection){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, BackupReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("Connection", connection)
        intent.putExtra("Bundle", bundle)

        val time = GetNextSchedule(connection.ScheduleType).timeInMillis

        Timber.d("Queued job ${connection.Name} for ${_calendarInstance.time}")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            connection.Id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_CANCEL_CURRENT
            }
        )

        alarmManager.set(
            AlarmManager.RTC,
            time,
            pendingIntent
        )
    }

    fun GetNextSchedule(scheduleType: ScheduleType) : Calendar {
        return when(scheduleType){
            ScheduleType.DAILY -> GetNextDailySchedule()
            ScheduleType.WEEKLY -> GetNextWeeklySchedule()
            ScheduleType.MONTHLY -> GetNextMonthlySchedule()
            ScheduleType.YEARLY -> GetNextYearlySchedule()
        }
    }

    private fun GetNextDailySchedule() : Calendar {
        val date = LocalDate.now().plusDays(1L)

        _calendarInstance.clear()
        _calendarInstance.set(date.year, date.monthValue - 1, date.dayOfMonth, 0, 0)

        return _calendarInstance
    }

    private fun GetNextWeeklySchedule() : Calendar {
        var date = LocalDate.now()

        val daysTillMonday = 8L - date.dayOfWeek.value

        date = date.plusDays(daysTillMonday)

        _calendarInstance.clear()
        _calendarInstance.set(date.year, date.monthValue - 1, date.dayOfMonth, 0, 0)

        return _calendarInstance
    }

    private fun GetNextMonthlySchedule() : Calendar {
        var date = LocalDate.now()
        date = date.minusDays(date.dayOfMonth - 1L).plusMonths(1L)

        _calendarInstance.clear()
        _calendarInstance.set(date.year, date.monthValue - 1, 1, 0, 0)

        return _calendarInstance
    }

    private fun GetNextYearlySchedule() : Calendar {
        val date = LocalDate.now()

        _calendarInstance.clear()
        _calendarInstance.set(date.year + 1, 0, 1, 0, 0)

        return _calendarInstance
    }

}