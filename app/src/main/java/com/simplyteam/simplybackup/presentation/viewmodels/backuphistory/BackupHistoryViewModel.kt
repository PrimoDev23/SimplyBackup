package com.simplyteam.simplybackup.presentation.viewmodels.backuphistory

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.BackupDetail
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.utils.MathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BackupHistoryViewModel @Inject constructor(
    private val NextCloudService: NextCloudService
) : ViewModel() {

    val ShowErrorLoading = mutableStateOf(false)

    val BackupToDelete = mutableStateOf<BackupDetail?>(null)

    val Loading = mutableStateOf(false)
    val BackupDetails = mutableStateOf(listOf<BackupDetail>())

    suspend fun InitValues(
        context: Context,
        connection: Connection
    ) {
        when (connection.ConnectionType) {
            ConnectionType.NextCloud -> {
                try {
                    Loading.value = true


                    val files = NextCloudService.GetFilesForConnection(
                        context,
                        connection
                    )
                    ShowErrorLoading.value = false

                    BuildBackupDetails(
                        connection,
                        files.sortedByDescending { file ->
                            file.uploadTimestamp
                        }
                    )
                } catch (ex: Exception) {
                    Timber.e(ex)

                    ShowErrorLoading.value = true
                } finally {
                    Loading.value = false
                }
            }
        }
    }

    private fun BuildBackupDetails(
        connection: Connection,
        files: List<RemoteFile>
    ) {
        val details = mutableListOf<BackupDetail>()

        for (file in files) {
            val fileName = ExtractFileNameFromRemotePath(
                connection = connection,
                file = file
            )

            val date = ExtractDateFromFileName(
                fileName
            )
            val size = MathUtil.GetBiggestFileSizeString(file.size)

            details.add(
                BackupDetail(
                    Connection = connection,
                    RemoteFile = file,
                    Size = size,
                    Date = date
                )
            )
        }

        BackupDetails.value = details
    }

    private fun ExtractFileNameFromRemotePath(
        connection: Connection,
        file: RemoteFile
    ): String =
        file.remotePath.removeRange(
            0,
            connection.RemotePath.length + 1
        )

    private fun ExtractDateFromFileName(fileName: String): String {
        val originalDate = fileName.split('-')
            .last()
            .removeSuffix(".zip")

        val date = LocalDateTime.parse(
            originalDate,
            Constants.PackagingFormatter
        )
        return date.format(Constants.HumanReadableFormatter)
    }

    fun ShowDeleteAlert(item: BackupDetail) {
        BackupToDelete.value = item
    }

    fun HideDeleteAlert() {
        BackupToDelete.value = null
    }

    suspend fun DeleteBackup(context: Context) {
        try {
            Loading.value = true

            BackupToDelete.value?.let { backup ->
                HideDeleteAlert()

                if(NextCloudService.DeleteFile(
                    context,
                    backup.Connection,
                    backup.RemoteFile
                )) {
                    DeleteBackupFromList(backup)
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        } finally {
            Loading.value = false
        }
    }

    private fun DeleteBackupFromList(item: BackupDetail) {
        val list = BackupDetails.value.toMutableList()

        list.remove(item)

        BackupDetails.value = list
    }

}