package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.exceptions.UpdateFailedException
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.presentation.views.IconProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class ConnectionConfigurationViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val schedulerService: SchedulerService,
    private val IconProvider: IconProvider
) : ViewModel() {
    private val _id = mutableStateOf(0L)

    val ConnectionType =
        mutableStateOf(com.simplyteam.simplybackup.data.models.ConnectionType.NextCloud)

    val ViewModelMap: MutableMap<ConnectionType, ConfigurationViewModelBase> = mutableMapOf()

    val CurrentPath = mutableStateOf("")
    val Paths = mutableStateOf(listOf<Path>())

    val RemotePath = mutableStateOf("")
    val WifiOnly = mutableStateOf(false)
    val ScheduleTypeDialogShown = mutableStateOf(false)
    val ScheduleType = mutableStateOf(com.simplyteam.simplybackup.data.models.ScheduleType.DAILY)

    fun GetIconProvider() = IconProvider

    fun UpdateScheduleType(scheduleType: ScheduleType) {
        ScheduleType.value = scheduleType
        ScheduleTypeDialogShown.value = false
    }

    suspend fun SaveConnection(context: ComponentActivity) {
        try {
            val connection = (ViewModelMap[ConnectionType.value])!!.GetBaseConnection()

            connection.Id = _id.value
            connection.RemotePath = RemotePath.value
            connection.WifiOnly = WifiOnly.value
            connection.Paths = Paths.value
            connection.ScheduleType = ScheduleType.value

            if (_id.value == 0L) {
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

            val list = Paths.value.toMutableList()

            list.add(path)

            Paths.value = list
            CurrentPath.value = ""
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun RemovePath(path: Path) {
        val list = Paths.value.toMutableList()

        list.remove(path)

        Paths.value = list
    }

    fun LoadData(connection: Connection) {
        _id.value = connection.Id

        ConnectionType.value = connection.ConnectionType
        RemotePath.value = connection.RemotePath
        WifiOnly.value = connection.WifiOnly
        Paths.value = connection.Paths
        ScheduleType.value = connection.ScheduleType

        ViewModelMap[connection.ConnectionType]!!.LoadData(connection)
    }
}