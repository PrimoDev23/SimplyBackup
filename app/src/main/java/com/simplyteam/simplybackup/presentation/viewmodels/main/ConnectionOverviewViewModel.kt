package com.simplyteam.simplybackup.presentation.viewmodels.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.presentation.activities.ConnectionConfigurationActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _schedulerService: SchedulerService
) : ViewModel() {

    fun GetConnections() = _connectionRepository.Connections.value

    suspend fun DeleteConnection(connection: Connection) {
        try {
            _connectionRepository.RemoveConnection(connection)

            _schedulerService.CancelBackup(connection)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun StartConfiguration(
        context: Context,
        item: Connection?
    ) {
        val intent = Intent(
            context,
            ConnectionConfigurationActivity::class.java
        )

        if (item != null) {
            intent.putExtra(
                "Connection",
                item
            )
        }

        context.startActivity(
            intent
        )
    }
}