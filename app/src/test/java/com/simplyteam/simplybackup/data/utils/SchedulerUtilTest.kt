package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.data.models.ScheduleType
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class SchedulerUtilTest {

    @Test
    fun GetNextScheduleTest() {
        val baseDate = LocalDate.of(2022, 1, 1)

        var nextSchedule = SchedulerUtil.GetNextSchedule(
            ScheduleType.DAILY,
            baseDate
        )

        assertEquals(LocalDateTime.of(2022, 1, 2, 0, 0), nextSchedule)

        nextSchedule = SchedulerUtil.GetNextSchedule(
            ScheduleType.WEEKLY,
            baseDate
        )

        assertEquals(LocalDateTime.of(2022, 1, 3, 0, 0), nextSchedule)

        nextSchedule = SchedulerUtil.GetNextSchedule(
            ScheduleType.MONTHLY,
            baseDate
        )

        assertEquals(LocalDateTime.of(2022, 2, 1, 0, 0), nextSchedule)

        nextSchedule = SchedulerUtil.GetNextSchedule(
            ScheduleType.YEARLY,
            baseDate
        )

        assertEquals(LocalDateTime.of(2023, 1, 1, 0, 0), nextSchedule)
    }
}