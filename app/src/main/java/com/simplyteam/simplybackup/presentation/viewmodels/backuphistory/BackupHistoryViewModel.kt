package com.simplyteam.simplybackup.presentation.viewmodels.backuphistory

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.BackupDetail
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.services.SFTPService
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.utils.FileUtil
import com.simplyteam.simplybackup.data.utils.MathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject
import com.simplyteam.simplybackup.data.models.RemoteFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class BackupHistoryViewModel @Inject constructor(
    private val _nextCloudService: NextCloudService,
    private val _sFTPService: SFTPService,
    private val _packagingService: PackagingService
) : ViewModel() {

    val ShowErrorLoading = mutableStateOf(false)

    val BackupToDelete = mutableStateOf<BackupDetail?>(null)
    val BackupToRestore = mutableStateOf<BackupDetail?>(null)
    val RestoreStatus = mutableStateOf(com.simplyteam.simplybackup.data.models.RestoreStatus.IDLE)

    val Loading = mutableStateOf(false)
    val BackupDetails = mutableStateOf(listOf<BackupDetail>())

    suspend fun InitValues(
        connection: Connection
    ) {
        try {
            Loading.value = true

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

            ShowErrorLoading.value = false

            BuildBackupDetails(
                connection,
                files.sortedByDescending { file ->
                    file.TimeStamp
                }
            )
        } catch (ex: Exception) {
            Timber.e(ex)

            ShowErrorLoading.value = true
        } finally {
            Loading.value = false
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

        BackupDetails.value = details
    }

    fun ShowDeleteAlert(item: BackupDetail) {
        BackupToDelete.value = item
    }

    fun HideDeleteAlert() {
        BackupToDelete.value = null
    }

    suspend fun DeleteBackup() {
        BackupToDelete.value?.let { backup ->
            try {
                Loading.value = true
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
                Loading.value = false
            }
        }
    }

    private fun DeleteBackupFromList(item: BackupDetail) {
        val list = BackupDetails.value.toMutableList()

        list.remove(item)

        BackupDetails.value = list
    }

    fun ShowRestoreAlert(detail: BackupDetail) {
        BackupToRestore.value = detail
    }

    fun HideRestoreAlert() {
        BackupToRestore.value = null
    }

    suspend fun RestoreBackup() {
        BackupToRestore.value?.let { backup ->
            try {
                RestoreStatus.value =
                    com.simplyteam.simplybackup.data.models.RestoreStatus.RESTORING

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

                _packagingService.RestorePackage(file)
                file.delete()

                RestoreStatus.value = com.simplyteam.simplybackup.data.models.RestoreStatus.SUCCESS
            } catch (ex: Exception) {
                Timber.e(ex)

                RestoreStatus.value = com.simplyteam.simplybackup.data.models.RestoreStatus.ERROR
            }
        }
    }

    fun HideRestoreFinishedAlert() {
        RestoreStatus.value = com.simplyteam.simplybackup.data.models.RestoreStatus.IDLE
    }

}