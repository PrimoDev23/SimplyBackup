package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.common.Constants
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
    private val _historyRepository: HistoryRepository,
    private val _connectionRepository: ConnectionRepository,
    private val _schedulerService: SchedulerService,
    private val _iconProvider: IconProvider
) : ViewModel() {
    fun GetConnections() = _connectionRepository.Connections.value

    fun BuildHistoryDataForConnection(connection: Connection) : HistoryData {
        val entries = _historyRepository.History.value.filter {
            it.ConnectionId == connection.Id
        }

        val calendar = _schedulerService.GetNextSchedule(connection.ScheduleType)

        return HistoryData(
            Name = connection.Name,
            Type = connection.ConnectionType,
            LastBackup = if(entries.isNotEmpty()) entries.last().Time else "-",
            NextBackup = LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).format(Constants.HumanReadableFormatter),
            LastBackupSize = if(entries.isNotEmpty()) MathUtil.GetBiggestFileSizeString(entries.last().Size) else "0 B",
            TotalBackedUpSize = MathUtil.GetBiggestFileSizeString(entries.sumOf {
                it.Size
            })
        )
    }

    fun GetIconProvider() = _iconProvider
}