package com.simplyteam.simplybackup.presentation.viewmodels.main

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.search.HistorySearchService
import com.simplyteam.simplybackup.data.utils.ActivityUtil.StartActivityWithAnimation
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.presentation.activities.BackupHistoryActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val _historyRepository: HistoryRepository,
    private val _historySearchService: HistorySearchService,
    private val _schedulerService: SchedulerService
) : ViewModel() {

    private val _openHistoryFlow = MutableSharedFlow<Connection>()
    val OpenHistoryFlow = _openHistoryFlow.asSharedFlow()

    val ListState = LazyListState()

    fun GetConnections() = _historySearchService.FilteredItems

    fun GetSearchText() = _historySearchService.GetSearchText()

    fun Search(searchText: String) {
        _historySearchService.Search(searchText)
    }

    fun ResetSearch() {
        _historySearchService.Search("")
    }

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

    fun OpenHistory(connection: Connection) {
        viewModelScope.launch {
            _openHistoryFlow.emit(connection)
        }
    }
}