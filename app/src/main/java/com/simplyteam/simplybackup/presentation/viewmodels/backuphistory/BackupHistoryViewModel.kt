package com.simplyteam.simplybackup.presentation.viewmodels.backuphistory

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.models.events.UIEvent
import com.simplyteam.simplybackup.data.models.events.backuphistory.BackupHistoryEvent
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.utils.FileUtil
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.presentation.uistates.backuphistory.BackupHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BackupHistoryViewModel @Inject constructor(
    private val _nextCloudService: NextCloudService,
    private val _sFTPService: SFTPService,
    private val _googleDriveService: GoogleDriveService,
    private val _packagingService: PackagingService
) : ViewModel() {

    val ListState = LazyListState()
    private val _restoreFinishedFlow = MutableSharedFlow<UIEvent.ShowSnackbar>()
    val RestoreFinishedFlow = _restoreFinishedFlow.asSharedFlow()

    var State by mutableStateOf(BackupHistoryState())

    fun OnEvent(event: BackupHistoryEvent){
        when(event){
            is BackupHistoryEvent.OnDeleteBackup -> {
                ShowDeleteAlert(event.Backup)
            }
            BackupHistoryEvent.OnDeleteConfirmed -> {
                DeleteBackup()
            }
            BackupHistoryEvent.OnDeleteDialogDismiss -> {
                HideDeleteAlert()
            }
            is BackupHistoryEvent.OnRestoreBackup -> {
                ShowRestoreAlert(event.Backup)
            }
            BackupHistoryEvent.OnRestoreConfirmed -> {
                RestoreBackup()
            }
            BackupHistoryEvent.OnRestoreDialogDismiss -> {
                HideRestoreAlert()
            }
        }
    }

    suspend fun InitValues(
        connection: Connection
    ) {
        try {
            State = State.copy(
                Loading = true
            )

            val files = withContext(Dispatchers.IO) {
                when (connection.ConnectionType) {
                    ConnectionType.NextCloud -> {
                        _nextCloudService.GetFilesForConnection(
                            connection
                        )

                    }
                    ConnectionType.SFTP -> {
                        _sFTPService.GetFilesForConnection(
                            connection
                        )
                    }
                    ConnectionType.GoogleDrive -> {
                        _googleDriveService.GetFilesForConnection(
                            connection
                        )
                    }
                }
            }

            BuildBackupDetails(
                connection,
                files.sortedByDescending { file ->
                    file.TimeStamp
                }
            )

            State = State.copy(
                LoadingError = false
            )
        } catch (ex: Exception) {
            Timber.e(ex)

            State = State.copy(
                LoadingError = true
            )
        } finally {
            State = State.copy(
                Loading = false
            )
        }
    }

    private fun BuildBackupDetails(
        connection: Connection,
        files: List<RemoteFile>
    ) {
        val details = mutableListOf<BackupDetail>()

        for (file in files) {
            val fileName = FileUtil.ExtractFileNameFromRemotePath(
                connection = connection,
                path = file.RemotePath
            )

            val date = FileUtil.ExtractDateFromFileName(
                fileName
            )
            val size = MathUtil.GetBiggestFileSizeString(file.Size)

            details.add(
                BackupDetail(
                    RemoteId = file.RemoteId,
                    Connection = connection,
                    RemotePath = file.RemotePath,
                    Size = size,
                    Date = date
                )
            )
        }

        State = State.copy(
            Backups = details
        )
    }

    private fun ShowDeleteAlert(item: BackupDetail) {
        State = State.copy(
            BackupToDelete = item
        )
    }

    private fun HideDeleteAlert() {
        State = State.copy(
            BackupToDelete = null
        )
    }

    private fun DeleteBackup() {
        viewModelScope.launch {
            State.BackupToDelete?.let { backup ->
                try {
                    State = State.copy(
                        Loading = true
                    )

                    HideDeleteAlert()

                    val result = withContext(Dispatchers.IO) {
                        when (backup.Connection.ConnectionType) {
                            ConnectionType.NextCloud -> {
                                _nextCloudService.DeleteFile(
                                    backup.Connection,
                                    backup.RemotePath
                                )
                            }
                            ConnectionType.SFTP -> {
                                _sFTPService.DeleteFile(
                                    backup.Connection,
                                    backup.RemotePath
                                )
                            }
                            ConnectionType.GoogleDrive -> {
                                _googleDriveService.DeleteFile(
                                    backup.Connection,
                                    backup.RemoteId
                                )
                            }
                        }
                    }

                    if (result) {
                        DeleteBackupFromList(backup)
                    }
                } catch (ex: Exception) {
                    Timber.e(ex)
                } finally {
                    State = State.copy(
                        Loading = false
                    )
                }
            }
        }
    }

    private fun DeleteBackupFromList(item: BackupDetail) {
        val list = State.Backups.toMutableList()

        list.remove(item)

        State = State.copy(
            Backups = list
        )
    }

    private fun ShowRestoreAlert(detail: BackupDetail) {
        State = State.copy(
            BackupToRestore = detail
        )
    }

    private fun HideRestoreAlert() {
        State = State.copy(
            BackupToRestore = null
        )
    }

    private fun RestoreBackup() {
        viewModelScope.launch {
            State.BackupToRestore?.let { backup ->
                try {
                    State = State.copy(
                        CurrentlyRestoring = true
                    )

                    HideRestoreAlert()

                    val file = withContext(Dispatchers.IO) {
                        when (backup.Connection.ConnectionType) {
                            ConnectionType.NextCloud -> {
                                _nextCloudService.DownloadFile(
                                    backup.Connection,
                                    backup.RemotePath
                                )
                            }
                            ConnectionType.SFTP -> {
                                _sFTPService.DownloadFile(
                                    backup.Connection,
                                    backup.RemotePath
                                )
                            }
                            ConnectionType.GoogleDrive -> {
                                _googleDriveService.DownloadFile(
                                    backup.Connection,
                                    backup.RemoteId
                                )
                            }
                        }
                    }

                    _packagingService.RestorePackage(
                        file,
                        backup.Connection
                    )
                    file.delete()

                    State = State.copy(
                        CurrentlyRestoring = false
                    )

                    _restoreFinishedFlow.emit(
                        UIEvent.ShowSnackbar(
                            UIText.StringResource(R.string.RestoringBackupSucceed)
                        )
                    )
                } catch (ex: Exception) {
                    Timber.e(ex)

                    State = State.copy(
                        CurrentlyRestoring = false
                    )

                    _restoreFinishedFlow.emit(
                        UIEvent.ShowSnackbar(
                            UIText.StringResource(R.string.RestoringBackupError)
                        )
                    )
                }
            }
        }
    }
}