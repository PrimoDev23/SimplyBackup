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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val _historySearchService: HistorySearchService
) : ViewModel() {

    private val _openHistoryFlow = MutableSharedFlow<Connection>()
    val OpenHistoryFlow = _openHistoryFlow.asSharedFlow()

    val ListState = LazyListState()

    fun GetHistoryData() = _historySearchService.FilteredItems

    fun GetSearchText() = _historySearchService.GetSearchText()

    fun Search(searchText: String) {
        _historySearchService.Search(searchText)
    }

    fun ResetSearch() {
        _historySearchService.Search("")
    }

    fun OpenHistory(connection: Connection) {
        viewModelScope.launch {
            _openHistoryFlow.emit(connection)
        }
    }
}