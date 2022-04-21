package com.simplyteam.simplybackup.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.receiver.BackupReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationService @Inject constructor(
    @ApplicationContext private val _context: Context
) {

    fun CreateNotificationChannel() {
        val notificationChannel = NotificationChannel(
            _context.getString(R.string.notification_channel_id),
            _context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        val notificationManager = _context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun ShowBackingUpNotification(connection: Connection) {
        val notificationManager = _context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(_context, _context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(_context.getString(R.string.BackingUpNotificationTitle))
            .setContentText(_context.getString(R.string.BackingUpNotificationText).format(connection.Name))
            .build()

        notificationManager.notify(connection.Id.toInt(), notification)
    }

    fun ShowErrorNotification(text: String, connection: Connection) {
        val notificationManager = _context.getSystemService(NotificationManager::class.java)

        val intent = Intent(_context, BackupReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("Connection", connection)
        intent.putExtra("Bundle", bundle)

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

        val notification = NotificationCompat.Builder(_context, _context.getString(R.string.notification_channel_id))
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

        notificationManager.notify(connection.Id.toInt(), notification)
    }

    fun ShowSuccessNotification(connection: Connection) {
        val notificationManager = _context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(_context, _context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(_context.getString(R.string.SuccessNotificationTitle))
            .setContentText(_context.getString(R.string.SuccessNotificationText).format(connection.Name))
            .build()

        notificationManager.notify(connection.Id.toInt(), notification)
    }

    fun HideBackingUpNotification(connection: Connection) {
        val notificationManager = _context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(connection.Id.toInt())
    }
}