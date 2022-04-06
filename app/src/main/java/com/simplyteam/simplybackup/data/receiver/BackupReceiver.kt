package com.simplyteam.simplybackup.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.models.exceptions.WifiNotEnabledException
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.services.NotificationService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.utils.MathUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class BackupReceiver : BroadcastReceiver() {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    @Inject
    lateinit var packagingService: PackagingService

    @Inject
    lateinit var nextCloudService: NextCloudService

    @Inject
    lateinit var schedulerService: SchedulerService

    @Inject
    lateinit var NotificationService: NotificationService

    @Inject
    lateinit var HistoryRepository: HistoryRepository

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let { context ->
            p1?.let { intent ->
                val extraBundle = intent.extras?.get("Bundle") as Bundle?

                extraBundle?.let { bundle ->
                    val conn = bundle.get("Connection") as Connection?

                    conn?.let { connection ->
                        if (!connection.WifiOnly || IsWifiConnected(context)) {
                            packagingService.CreatePackage(context, connection)
                                .onSuccess { file ->
                                    GlobalScope.launch {
                                        NotificationService.ShowBackingUpNotification(
                                            context,
                                            connection
                                        )
                                        HistoryRepository.ContextOnlyInit(context)
                                        val uploadResult = nextCloudService.UploadFile(
                                            context,
                                            connection,
                                            file
                                        )

                                        NotificationService.HideBackingUpNotification(
                                            context,
                                            connection
                                        )

                                        uploadResult
                                            .onSuccess { result ->
                                                if (result.isSuccess) {
                                                    AddHistoryEntry(connection, file, true)

                                                    NotificationService.ShowSuccessNotification(
                                                        context,
                                                        connection
                                                    )
                                                } else {
                                                    Timber.e(result.exception)

                                                    NotificationService.ShowErrorNotification(
                                                        context,
                                                        context.getString(R.string.ErrorUpload).format(connection.Name, result.code.name),
                                                        connection
                                                    )
                                                }
                                            }
                                            .onFailure {
                                                Timber.e(it)

                                                NotificationService.ShowErrorNotification(
                                                    context,
                                                    context.getString(R.string.ErrorException).format(connection.Name),
                                                    connection
                                                )
                                            }

                                        file.delete()
                                    }
                                }
                                .onFailure {
                                    Timber.e(it)

                                    NotificationService.ShowErrorNotification(
                                        context,
                                        context.getString(R.string.ErrorPackaging).format(connection.Name),
                                        connection
                                    )
                                }
                        } else {
                            Timber.e(WifiNotEnabledException())

                            NotificationService.ShowErrorNotification(
                                context,
                                context.getString(R.string.ErrorWifi).format(connection.Name),
                                connection
                            )
                        }

                        //Reschedule even if the backup fails
                        schedulerService.ScheduleBackup(context, connection)
                    }
                }
            }
        }
    }

    private fun IsWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        }
        return false
    }

    suspend fun AddHistoryEntry(connection: Connection, file: File, succeed: Boolean) {
        val entry = HistoryEntry(
            ConnectionId = connection.Id,
            Time = LocalDateTime.now().format(formatter),
            Size = file.length(),
            Succeed = succeed
        )

        HistoryRepository.InsertHistoryEntry(entry)
            .onFailure {
                Timber.e(it)
            }
            .onSuccess {
                Timber.d("Added ID $it to history")
            }
    }

}