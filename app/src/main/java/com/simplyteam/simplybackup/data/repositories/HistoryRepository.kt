package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.data.utils.SchedulerUtil
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val _historyDao: HistoryDao
) {
    var HistoryEntries = listOf<HistoryEntry>()
    var HistoryData by mutableStateOf(listOf<HistoryData>())

    fun GetFlow() = _historyDao.GetHistoryFlow()

    fun BuildHistoryData(connections: List<Connection>) {
        val data = mutableListOf<HistoryData>()

        for (connection in connections) {
            val calendar = SchedulerUtil.GetNextSchedule(connection.ScheduleType)

            val entries = HistoryEntries.filter {
                it.ConnectionId == connection.Id
            }

            data.add(
                HistoryData(
                    Connection = connection,
                    LastBackup = if (entries.isNotEmpty()) entries.last().Time else "-",
                    NextBackup = LocalDateTime.ofInstant(
                        calendar.toInstant(),
                        calendar.timeZone.toZoneId()
                    )
                        .format(Constants.HumanReadableFormatter),
                    LastBackupSize = if (entries.isNotEmpty()) MathUtil.GetBiggestFileSizeString(entries.last().Size) else "0 B",
                    TotalBackedUpSize = MathUtil.GetBiggestFileSizeString(entries.sumOf {
                        it.Size
                    })
                )
            )
        }

        HistoryData = data
    }

    suspend fun InsertHistoryEntry(entry: HistoryEntry): Result<Long> {
        return try {
            Result.success(_historyDao.InsertHistoryEntry(entry))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

}