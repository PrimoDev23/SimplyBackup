package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.data.models.ScheduleType
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object SchedulerUtil {
    fun GetNextSchedule(
        scheduleType: ScheduleType,
        baseDate: LocalDate = LocalDate.now()
    ) : LocalDateTime {
        return when(scheduleType){
            ScheduleType.DAILY -> GetNextDailySchedule(baseDate)
            ScheduleType.WEEKLY -> GetNextWeeklySchedule(baseDate)
            ScheduleType.MONTHLY -> GetNextMonthlySchedule(baseDate)
            ScheduleType.YEARLY -> GetNextYearlySchedule(baseDate)
        }
    }

    private fun GetNextDailySchedule(baseDate: LocalDate) =
        baseDate.plusDays(1L).atStartOfDay()

    private fun GetNextWeeklySchedule(baseDate: LocalDate) =
        baseDate.plusDays(8L - baseDate.dayOfWeek.value).atStartOfDay()

    private fun GetNextMonthlySchedule(baseDate: LocalDate) : LocalDateTime =
        baseDate.withDayOfMonth(1).plusMonths(1).atStartOfDay()

    private fun GetNextYearlySchedule(baseDate: LocalDate) : LocalDateTime =
        LocalDateTime.of(baseDate.year + 1, 1, 1, 0, 0)

}