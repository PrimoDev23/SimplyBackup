package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.exceptions.UpdateFailedException
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.utils.ActivityUtil.FinishActivityWithAnimation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
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

    var SelectedConnectionType by mutableStateOf(ConnectionType.NextCloud)

    val ViewModelMap: MutableMap<ConnectionType, ConfigurationViewModelBase> = mutableMapOf()

    var Paths = listOf<Path>()

    var BackupPassword by mutableStateOf("")
    var WifiOnly by mutableStateOf(false)
    var SelectedScheduleType by mutableStateOf(ScheduleType.DAILY)

    fun SaveConnection() {
        viewModelScope.launch {
            try {
                val connection = (ViewModelMap[SelectedConnectionType])!!.GetBaseConnection()

                connection.Id = _id

                connection.BackupPassword = BackupPassword
                connection.WifiOnly = WifiOnly
                connection.Paths = Paths
                connection.ScheduleType = SelectedScheduleType

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

    fun LoadData(connection: Connection) {
        _id = connection.Id

        SelectedConnectionType = connection.ConnectionType
        BackupPassword = connection.BackupPassword
        WifiOnly = connection.WifiOnly
        Paths = connection.Paths
        SelectedScheduleType = connection.ScheduleType

        ViewModelMap[connection.ConnectionType]!!.LoadData(connection)
    }
}