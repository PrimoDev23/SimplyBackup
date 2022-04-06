package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
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
    val connectionRepository: ConnectionRepository,
    val schedulerService: SchedulerService,
    val IconProvider: IconProvider
) : ViewModel() {
    private val _id = mutableStateOf(0L)

    val ConnectionType = mutableStateOf<ConnectionType?>(null)

    val Name = mutableStateOf("")
    val URL = mutableStateOf("")
    val Username = mutableStateOf("")
    val Password = mutableStateOf("")
    val RemotePath = mutableStateOf("")
    val WifiOnly = mutableStateOf(false)

    val CurrentPath = mutableStateOf("")
    val Paths = mutableStateOf(listOf<Path>())

    val ScheduleTypeDialogShown = mutableStateOf(false)
    val ScheduleType = mutableStateOf(com.simplyteam.simplybackup.data.models.ScheduleType.DAILY)

    fun GetIconProvider() = IconProvider

    fun UpdateScheduleType(scheduleType: ScheduleType){
        ScheduleType.value = scheduleType
        ScheduleTypeDialogShown.value = false
    }

    suspend fun SaveConnection(context: ComponentActivity) {
        when {
            ConnectionType.value == null -> {
                Timber.e(FieldNotFilledException("ConnectionType"))
                return
            }
            Name.value.isEmpty() -> {
                Timber.e(FieldNotFilledException("Name"))
                return
            }
            URL.value.isEmpty() -> {
                Timber.e(FieldNotFilledException("URL"))
                return
            }
            Username.value.isEmpty() -> {
                Timber.e(FieldNotFilledException("Username"))
                return
            }
            Password.value.isEmpty() -> {
                Timber.e(FieldNotFilledException("Password"))
                return
            }
            else -> {
                val connection = Connection(
                    Id = _id.value,
                    Name = Name.value,
                    ConnectionType = ConnectionType.value!!,
                    URL = URL.value,
                    Username = Username.value,
                    Password = Password.value,
                    RemotePath = RemotePath.value,
                    WifiOnly = WifiOnly.value,
                    Paths = Paths.value,
                    ScheduleType = ScheduleType.value
                )

                if (_id.value == 0L) {
                    val result = connectionRepository.InsertConnection(
                        connection
                    )

                    result
                        .onSuccess { id ->
                            connection.Id = id
                            schedulerService.ScheduleBackup(context, connection)
                            context.finish()
                        }
                        .onFailure {
                            Timber.e(it)
                        }
                } else {
                    val result = connectionRepository.UpdateConnection(
                        connection
                    )

                    result
                        .onSuccess { updatedRows ->
                            if (updatedRows > 0) {
                                schedulerService.ScheduleBackup(context, connection)
                                context.finish()
                            } else {
                                Timber.e(UpdateFailedException())
                            }
                        }
                        .onFailure {
                            Timber.e(it)
                        }
                }
            }
        }

    }

    private fun CreatePathObjectFromStringPath(path: String): Result<Path> {
        val file = File(path)

        if (!file.exists()) {
            return Result.failure(FileNotFoundException(path))
        }

        return if (file.isDirectory) {
            Result.success(
                Path(
                    Path = file.absolutePath,
                    Type = PathType.DIRECTORY
                )
            )
        } else {
            Result.success(
                Path(
                    Path = file.absolutePath,
                    Type = PathType.FILE
                )
            )
        }
    }

    fun AddPath(stringPath: String) {
        val createdPath = CreatePathObjectFromStringPath(stringPath)

        createdPath
            .onSuccess { path ->
                val list = Paths.value.toMutableList()

                list.add(path)

                Paths.value = list
                CurrentPath.value = ""
            }
            .onFailure {
                Timber.e(it)
            }
    }

    fun RemovePath(path: Path) {
        val list = Paths.value.toMutableList()

        list.remove(path)

        Paths.value = list
    }

    fun LoadData(connection: Connection) {
        _id.value = connection.Id

        Name.value = connection.Name
        URL.value = connection.URL
        Username.value = connection.Username
        Password.value = connection.Password
        ConnectionType.value = connection.ConnectionType
        RemotePath.value = connection.RemotePath
        WifiOnly.value = connection.WifiOnly
        Paths.value = connection.Paths
        ScheduleType.value = connection.ScheduleType
    }
}