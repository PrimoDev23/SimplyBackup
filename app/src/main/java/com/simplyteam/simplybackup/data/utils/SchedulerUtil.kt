package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.data.models.ScheduleType
import java.time.Duration
import java.time.LocalDate
import java.util.*

object SchedulerUtil {

    private val _calendarInstance = Calendar.getInstance()

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