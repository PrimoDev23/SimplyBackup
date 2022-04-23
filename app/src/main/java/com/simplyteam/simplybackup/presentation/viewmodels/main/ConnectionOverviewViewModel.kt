package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.UIText
import com.simplyteam.simplybackup.data.models.events.UIEvent
import com.simplyteam.simplybackup.data.models.events.main.ConnectionOverviewEvent
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
    private val _connectionRemovedFlow = MutableSharedFlow<UIEvent.ShowSnackbar>()
    val ConnectionRemovedFlow = _connectionRemovedFlow.asSharedFlow()

    private val _runBackupFlow = MutableSharedFlow<ConnectionOverviewEvent.OnBackupConnection>()
    val RunBackupFlow = _runBackupFlow.asSharedFlow()

    fun OnEvent(event: ConnectionOverviewEvent){
        when(event){
            is ConnectionOverviewEvent.OnDeleteConnection -> {
                DeleteConnection(event.Connection)
            }
            is ConnectionOverviewEvent.OnBackupConnection -> {
                viewModelScope.launch {
                    _runBackupFlow.emit(event)
                }
            }
            is ConnectionOverviewEvent.Search -> {
                Search(event.Value)
            }
            ConnectionOverviewEvent.ResetSearch -> {
                ResetSearch()
            }
        }
    }

    fun GetConnections() = _connectionSearchService.FilteredItems

    fun GetSearchText() = _connectionSearchService.GetSearchText()

    private fun Search(searchText: String) {
        _connectionSearchService.Search(searchText)
    }

    private fun ResetSearch() {
        _connectionSearchService.Search("")
    }

    private fun DeleteConnection(
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
                    UIEvent.ShowSnackbar(
                        Message = UIText.StringResource(
                            R.string.ConnectionRemoved,
                            connection.Name
                        ),
                        Action = UIText.StringResource(R.string.Undo),
                        ActionClicked = {
                            RestoreConnection(connection)
                        },
                        Dismissed = {
                            FinishConnectionRemoval(connection)
                        }
                    )
                )
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    private fun FinishConnectionRemoval(connection: Connection) {
        viewModelScope.launch {
            _connectionRepository.RemoveConnection(connection)

            _schedulerService.CancelBackup(connection)
        }
    }

    private fun RestoreConnection(connection: Connection) {
        viewModelScope.launch {
            _connectionRepository.UpdateConnection(
                connection.apply {
                    TemporarilyDeleted = false
                }
            )
        }
    }
}