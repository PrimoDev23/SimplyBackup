package com.simplyteam.simplybackup.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.NotificationService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.cloudservices.seafile.SeaFileService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val _context: Context,
    @Assisted _params: WorkerParameters,
    private val _packagingService: PackagingService,
    private val _nextCloudService: NextCloudService,
    private val _sftpService: SFTPService,
    private val _googleDriveService: GoogleDriveService,
    private val _seaFileService: SeaFileService,
    private val _notificationService: NotificationService,
    private val _schedulerService: SchedulerService,
    private val _connectionRepository: ConnectionRepository,
    private val _historyRepository: HistoryRepository
) : CoroutineWorker(
    _context,
    _params
) {
    override suspend fun doWork(): Result {
        val id = inputData.getLong(
            "ConnectionId",
            -1
        )

        if (id == -1L) {
            return Result.failure()
        }

        val connection = _connectionRepository.GetConnection(id)

        try {
            _notificationService.ShowBackingUpNotification(
                connection
            )

            _packagingService.CreatePackage(
                _context.filesDir.absolutePath,
                connection
            )
                .onSuccess { file ->
                    val uploadResult = when (connection.ConnectionType) {
                        ConnectionType.NextCloud -> {
                            _nextCloudService.UploadFile(
                                connection,
                                file
                            )
                        }
                        ConnectionType.SFTP -> {
                            _sftpService.UploadFile(
                                connection,
                                file
                            )
                        }
                        ConnectionType.GoogleDrive -> {
                            _googleDriveService.UploadFile(
                                connection,
                                file
                            )
                        }
                        ConnectionType.SeaFile -> {
                            _seaFileService.UploadFile(
                                connection,
                                file
                            )
                        }
                    }

                    _notificationService.HideBackingUpNotification(
                        connection
                    )

                    uploadResult
                        .onSuccess {
                            AddHistoryEntry(
                                connection,
                                file,
                                true
                            )

                            _notificationService.ShowSuccessNotification(
                                connection
                            )
                        }
                        .onFailure {
                            Timber.e(it)

                            _notificationService.ShowErrorNotification(
                                _context.getString(R.string.ErrorException)
                                    .format(connection.Name),
                                connection
                            )
                        }

                    file.delete()

                    return Result.success()
                }
                .onFailure {
                    Timber.e(it)

                    _notificationService.ShowErrorNotification(
                        _context.getString(R.string.ErrorPackaging)
                            .format(connection.Name),
                        connection
                    )
                }

            return Result.failure()
        } finally {
            _schedulerService.ScheduleBackup(connection)
        }
    }

    private suspend fun AddHistoryEntry(
        connection: Connection,
        file: File,
        succeed: Boolean
    ) {
        val entry = HistoryEntry(
            ConnectionId = connection.Id,
            Time = LocalDateTime.now()
                .format(Constants.HumanReadableFormatter),
            Size = file.length(),
            Succeed = succeed
        )

        _historyRepository.InsertHistoryEntry(entry)
            .onFailure {
                Timber.e(it)
            }
            .onSuccess {
                Timber.d("Added ID $it to history")
            }
    }
}