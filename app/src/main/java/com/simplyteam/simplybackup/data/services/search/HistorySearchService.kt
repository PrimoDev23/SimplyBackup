package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.data.utils.SchedulerUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistorySearchService @Inject constructor(
    _historyRepository: HistoryRepository,
    _connectionRepository: ConnectionRepository
) : SearchServiceBase<HistoryData>() {

    override var FilteredItems: Flow<List<HistoryData>> = _historyRepository.Flow
        .combine(_connectionRepository.Flow) { entries, connections ->
            BuildHistoryData(
                connections,
                entries
            )
        }
        .combine(SearchText) { data, search ->
            data.filter {
                it.Connection.Name.contains(
                    search,
                    true
                )
            }
        }

    private fun BuildHistoryData(
        connections: List<Connection>,
        historyEntries: List<HistoryEntry>
    ): List<HistoryData> {
        val data = mutableListOf<HistoryData>()

        for (connection in connections) {
            val nextScheduleTime = SchedulerUtil.GetNextSchedule(connection.ScheduleType)

            val entries = historyEntries.filter {
                it.ConnectionId == connection.Id
            }

            data.add(
                HistoryData(
                    Connection = connection,
                    LastBackup = if (entries.isNotEmpty()) entries.last().Time else "-",
                    NextBackup =  nextScheduleTime.format(Constants.HumanReadableFormatter),
                    LastBackupSize = if (entries.isNotEmpty()) MathUtil.GetBiggestFileSizeString(entries.last().Size) else "0 B",
                    TotalBackedUpSize = MathUtil.GetBiggestFileSizeString(entries.sumOf {
                        it.Size
                    })
                )
            )
        }

        return data
    }
}