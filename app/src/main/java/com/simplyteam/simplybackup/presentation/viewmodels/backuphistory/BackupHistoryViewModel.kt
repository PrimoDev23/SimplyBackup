package com.simplyteam.simplybackup.presentation.viewmodels.backuphistory

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.services.SFTPService
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.utils.FileUtil
import com.simplyteam.simplybackup.data.utils.MathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BackupHistoryViewModel @Inject constructor(
    private val _nextCloudService: NextCloudService,
    private val _sFTPService: SFTPService,
    private val _packagingService: PackagingService
) : ViewModel() {

    val ListState = LazyListState()

    val RestoreSnackbarState = SnackbarHostState()
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

            val files = when (connection.ConnectionType) {
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
            }

            ShowErrorLoading = false

            BuildBackupDetails(
                connection,
                files.sortedByDescending { file ->
                    file.TimeStamp
                }
            )
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

    suspend fun DeleteBackup() {
        BackupToDelete?.let { backup ->
            try {
                Loading = true
                HideDeleteAlert()

                val result = when (backup.Connection.ConnectionType) {
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

    suspend fun RestoreBackup(context: Context) {
        BackupToRestore?.let { backup ->
            try {
                CurrentlyRestoring =
                    true

                HideRestoreAlert()

                val file = when (backup.Connection.ConnectionType) {
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
                }

                _packagingService.RestorePackage(file, backup.Connection)
                file.delete()

                CurrentlyRestoring = false
                RestoreSnackbarState.showSnackbar(context.getString(R.string.RestoringBackupSucceed))
            } catch (ex: Exception) {
                Timber.e(ex)

                CurrentlyRestoring = false
                RestoreSnackbarState.showSnackbar(context.getString(R.string.RestoringBackupError))
            }
        }
    }
}