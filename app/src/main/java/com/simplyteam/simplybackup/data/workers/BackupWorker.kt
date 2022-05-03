package com.simplyteam.simplybackup.data.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.receiver.RunBackupReceiver
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.cloudservices.seafile.SeaFileService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val _context: Context,
    @Assisted _params: WorkerParameters,
    private val _packagingService: PackagingService,
    private val _nextCloudService: NextCloudService,
    private val _sftpService: SFTPService,
    private val _googleDriveService: GoogleDriveService,
    private val _seaFileService: SeaFileService,
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
            //This can be long running
            setForeground(
                BuildBackingUpForegroundInfo(
                    connection,
                    _context.getString(R.string.CreatingArchive)
                        .format(connection.Name)
                )
            )

            _packagingService.CreatePackage(
                _context.cacheDir.absolutePath,
                connection
            )
                .onSuccess { file ->
                    setForeground(
                        BuildBackingUpForegroundInfo(
                            connection,
                            _context.getString(R.string.UploadingBackup)
                                .format(connection.Name)
                        )
                    )

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

                    uploadResult
                        .onSuccess {
                            AddHistoryEntry(
                                connection,
                                file,
                                true
                            )

                            ShowSuccessNotification(connection)
                        }
                        .onFailure {
                            Timber.e(it)

                            ShowErrorNotification(
                                connection,
                                _context.getString(R.string.ErrorException)
                                    .format(connection.Name)
                            )
                        }

                    //Remove the file, no matter of the result
                    file.delete()

                    return Result.success()
                }
                .onFailure {
                    Timber.e(it)

                    ShowErrorNotification(
                        connection,
                        _context.getString(R.string.ErrorPackaging)
                            .format(connection.Name)
                    )
                }

            return Result.failure()
        } finally {
            _schedulerService.ScheduleBackup(connection)
        }
    }

    private fun BuildBackingUpForegroundInfo(
        connection: Connection,
        text: String
    ): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            _context,
            _context.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(_context.getString(R.string.BackingUpNotificationTitle))
            .setContentText(
                text
            )
            .setOngoing(true)
            .build()

        return ForegroundInfo(
            connection.Id.toInt(),
            notification
        )
    }

    private fun ShowSuccessNotification(connection: Connection) {
        val notification = NotificationCompat.Builder(
            _context,
            _context.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(_context.getString(R.string.SuccessNotificationTitle))
            .setContentText(
                _context.getString(R.string.SuccessNotificationText)
                    .format(connection.Name)
            )
            .build()

        val notificationManager = _context.getSystemService(NotificationManager::class.java)

        notificationManager.notify(
            connection.Id.toInt(),
            notification
        )
    }

    private fun ShowErrorNotification(
        connection: Connection,
        text: String
    ) {
        val intent = Intent(
            _context,
            RunBackupReceiver::class.java
        )
        val bundle = Bundle()
        bundle.putSerializable(
            "Connection",
            connection
        )
        intent.putExtra(
            "Bundle",
            bundle
        )

        val pendingIntent = PendingIntent.getBroadcast(
            _context,
            connection.Id.toInt() + Constants.NOTIFICATION_ID_OFFSET,
            intent,
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_CANCEL_CURRENT
            }
        )

        val notification = NotificationCompat.Builder(
            _context,
            _context.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(_context.getString(R.string.ErrorNotificationTitle))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle())
            .addAction(
                0,
                _context.getString(R.string.Retry),
                pendingIntent
            )
            .build()

        val notificationManager = _context.getSystemService(NotificationManager::class.java)

        notificationManager.notify(
            connection.Id.toInt(),
            notification
        )
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