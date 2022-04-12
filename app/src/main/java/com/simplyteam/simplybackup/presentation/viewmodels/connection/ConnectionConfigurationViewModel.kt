package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.exceptions.UpdateFailedException
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class ConnectionConfigurationViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val schedulerService: SchedulerService
) : ViewModel() {
    private var _id = 0L

    var SelectedConnectionType by
        mutableStateOf(ConnectionType.NextCloud)

    val ViewModelMap: MutableMap<ConnectionType, ConfigurationViewModelBase> = mutableMapOf()

    var CurrentPath by mutableStateOf("")
    var Paths by mutableStateOf(listOf<Path>())

    var BackupPassword by mutableStateOf("")
    var WifiOnly by mutableStateOf(false)
    var ScheduleTypeDialogShown by mutableStateOf(false)
    var SelectedScheduleType by mutableStateOf(ScheduleType.DAILY)

    fun UpdateScheduleType(scheduleType: ScheduleType) {
        SelectedScheduleType = scheduleType
        ScheduleTypeDialogShown = false
    }

    suspend fun SaveConnection(context: ComponentActivity) {
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
                context.finish()
            } else {
                val updatedRows = connectionRepository.UpdateConnection(
                    connection
                )

                if (updatedRows > 0) {
                    schedulerService.ScheduleBackup(
                        connection
                    )
                    context.finish()
                } else {
                    throw UpdateFailedException()
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun CreatePathObjectFromStringPath(path: String): Path {
        val file = File(path)

        if (!file.exists()) {
            throw FileNotFoundException(path)
        }

        return if (file.isDirectory) {
            Path(
                Path = file.absolutePath,
                Type = PathType.DIRECTORY
            )
        } else {
            Path(
                Path = file.absolutePath,
                Type = PathType.FILE
            )
        }
    }

    fun AddPath(stringPath: String) {
        try {
            val path = CreatePathObjectFromStringPath(stringPath)

            val list = Paths.toMutableList()

            list.add(path)

            Paths = list
            CurrentPath = ""
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun RemovePath(path: Path) {
        val list = Paths.toMutableList()

        list.remove(path)

        Paths = list
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