package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.Event
import com.simplyteam.simplybackup.data.models.UIText
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _connectionSearchService: ConnectionSearchService,
    private val _schedulerService: SchedulerService
) : ViewModel() {

    val ListState = LazyListState()
    private val _connectionRemovedFlow = MutableSharedFlow<Event.ConnectionRemovedEvent>()
    val ConnectionRemovedFlow = _connectionRemovedFlow.asSharedFlow()

    private val _backupStartedFlow = MutableSharedFlow<Event.SimpleTextEvent>()
    val BackupStartedFlow = _backupStartedFlow.asSharedFlow()

    fun GetConnections() = _connectionSearchService.FilteredItems

    fun GetSearchText() = _connectionSearchService.GetSearchText()

    fun Search(searchText: String) {
        _connectionSearchService.Search(searchText)
    }

    fun ResetSearch() {
        _connectionSearchService.Search("")
    }

    fun DeleteConnection(
        connection: Connection
    ) {
        viewModelScope.launch {
            try {
                _connectionRepository.UpdateConnection(
                    connection.apply {
                        TemporarilyDeleted = true
                    }
                )

                _connectionRemovedFlow.emit(
                    Event.ConnectionRemovedEvent(
                        text = UIText.StringResource(
                            R.string.ConnectionRemoved,
                            connection.Name
                        ),
                        action = UIText.StringResource(R.string.Undo),
                        connection = connection
                    )
                )
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    fun FinishConnectionRemoval(connection: Connection) {
        viewModelScope.launch {
            _connectionRepository.RemoveConnection(connection)

            _schedulerService.CancelBackup(connection)
        }
    }

    fun RestoreConnection(connection: Connection) {
        viewModelScope.launch {
            _connectionRepository.UpdateConnection(
                connection.apply {
                    TemporarilyDeleted = false
                }
            )
        }
    }

    fun ShowBackupSnackbar(
        connection: Connection
    ) {
        viewModelScope.launch {
            _backupStartedFlow.emit(
                Event.SimpleTextEvent(
                    UIText.StringResource(R.string.BackupStarted, connection.Name)
                )
            )
        }
    }
}