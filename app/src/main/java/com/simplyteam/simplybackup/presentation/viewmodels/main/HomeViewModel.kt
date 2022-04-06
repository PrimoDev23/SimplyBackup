package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.presentation.views.IconProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val HistoryRepository: HistoryRepository,
    val ConnectionRepository: ConnectionRepository,
    val SchedulerService: SchedulerService,
    val IconProvider: IconProvider
) : ViewModel() {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    fun GetConnections() = ConnectionRepository.Connections.value

    fun BuildHistoryDataForConnection(connection: Connection) : HistoryData {
        val entries = HistoryRepository.History.value.filter {
            it.ConnectionId == connection.Id
        }

        val calendar = SchedulerService.GetNextSchedule(connection.ScheduleType)

        return HistoryData(
            Name = connection.Name,
            Type = connection.ConnectionType,
            LastBackup = if(entries.isNotEmpty()) entries.last().Time else "-",
            NextBackup = LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).format(formatter),
            LastBackupSize = if(entries.isNotEmpty()) MathUtil.GetBiggestFileSizeString(entries.last().Size) else "0 B",
            TotalBackedUpSize = MathUtil.GetBiggestFileSizeString(entries.sumOf {
                it.Size
            })
        )
    }

    fun GetIconProvider() = IconProvider
}