package com.simplyteam.simplybackup.presentation.viewmodels.backuphistory

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.utils.FileUtil
import com.simplyteam.simplybackup.data.utils.MathUtil
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
    private val _restoreFinishedFlow = MutableSharedFlow<Event.SimpleTextEvent>()
    val RestoreFinishedFlow = _restoreFinishedFlow.asSharedFlow()

    var ShowErrorLoading by mutableStateOf(false)

    var BackupToDelete by mutableStateOf<BackupDetail?>(null)
    var BackupToRestore by mutableStateOf<BackupDetail?>(null)
    var CurrentlyRestoring by mutableStateOf(false)

    var Loading by mutableStateOf(false)
    var BackupDetails by mutableStateOf(listOf<BackupDetail>())

    suspend fun InitValues(
        connection: Connection
    ) {
        try {
            Loading = true

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

            ShowErrorLoading = false
        } catch (ex: Exception) {
            Timber.e(ex)

            ShowErrorLoading = true
        } finally {
            Loading = false
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

        BackupDetails = details
    }

    fun ShowDeleteAlert(item: BackupDetail) {
        BackupToDelete = item
    }

    fun HideDeleteAlert() {
        BackupToDelete = null
    }

    fun DeleteBackup() {
        viewModelScope.launch {
            BackupToDelete?.let { backup ->
                try {
                    Loading = true
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
                    Loading = false
                }
            }
        }
    }

    private fun DeleteBackupFromList(item: BackupDetail) {
        val list = BackupDetails.toMutableList()

        list.remove(item)

        BackupDetails = list
    }

    fun ShowRestoreAlert(detail: BackupDetail) {
        BackupToRestore = detail
    }

    fun HideRestoreAlert() {
        BackupToRestore = null
    }

    fun RestoreBackup() {
        viewModelScope.launch {
            BackupToRestore?.let { backup ->
                try {
                    CurrentlyRestoring =
                        true

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

                    CurrentlyRestoring = false
                    _restoreFinishedFlow.emit(
                        Event.SimpleTextEvent(
                            UIText.StringResource(R.string.RestoringBackupSucceed)
                        )
                    )
                } catch (ex: Exception) {
                    Timber.e(ex)

                    CurrentlyRestoring = false
                    _restoreFinishedFlow.emit(
                        Event.SimpleTextEvent(
                            UIText.StringResource(R.string.RestoringBackupError)
                        )
                    )
                }
            }
        }
    }
}