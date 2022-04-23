package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.events.connection.ConnectionConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.UpdateFailedException
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.presentation.uistates.connection.ConnectionConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionConfigurationViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val schedulerService: SchedulerService
) : ViewModel() {

    private val _finishFlow = MutableSharedFlow<Unit>()
    val FinishFlow = _finishFlow.asSharedFlow()

    val ScrollState = ScrollState(0)

    private var _id = 0L
    var Paths = listOf<Path>()

    val ViewModelMap: MutableMap<ConnectionType, ConfigurationViewModelBase> = mutableMapOf()

    var State by mutableStateOf(ConnectionConfigurationState())

    fun OnEvent(event: ConnectionConfigurationEvent) {
        when (event) {
            is ConnectionConfigurationEvent.OnBackupPasswordChange -> {
                State = State.copy(
                    BackupPassword = event.Value
                )
            }
            is ConnectionConfigurationEvent.OnSelectedConnectionTypeChange -> {
                State = State.copy(
                    SelectedConnectionType = event.Value
                )
            }
            is ConnectionConfigurationEvent.OnSelectedScheduleTypeChange -> {
                State = State.copy(
                    SelectedScheduleType = event.Value
                )
            }
            is ConnectionConfigurationEvent.OnWifiOnlyChange -> {
                State = State.copy(
                    WifiOnly = event.Value
                )
            }
            is ConnectionConfigurationEvent.OnLoadData -> {
                LoadData(event.Connection)
            }
            ConnectionConfigurationEvent.OnSaveConnection -> {
                SaveConnection()
            }
        }
    }

    private fun SaveConnection() {
        viewModelScope.launch {
            try {
                val connection = (ViewModelMap[State.SelectedConnectionType])!!.GetBaseConnection()

                connection.Id = _id

                connection.BackupPassword = State.BackupPassword
                connection.WifiOnly = State.WifiOnly
                connection.Paths = Paths
                connection.ScheduleType = State.SelectedScheduleType

                if (_id == 0L) {
                    val id = connectionRepository.InsertConnection(
                        connection
                    )

                    connection.Id = id
                    schedulerService.ScheduleBackup(
                        connection
                    )

                    _finishFlow.emit(
                        Unit
                    )
                } else {
                    val updatedRows = connectionRepository.UpdateConnection(
                        connection
                    )

                    if (updatedRows > 0) {
                        schedulerService.ScheduleBackup(
                            connection
                        )

                        _finishFlow.emit(
                            Unit
                        )
                    } else {
                        throw UpdateFailedException()
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    private fun LoadData(connection: Connection) {
        _id = connection.Id
        Paths = connection.Paths

        State = State.copy(
            SelectedConnectionType = connection.ConnectionType,
            BackupPassword = connection.BackupPassword,
            WifiOnly = connection.WifiOnly,
            SelectedScheduleType = connection.ScheduleType
        )

        ViewModelMap[connection.ConnectionType]!!.LoadData(connection)
    }
}