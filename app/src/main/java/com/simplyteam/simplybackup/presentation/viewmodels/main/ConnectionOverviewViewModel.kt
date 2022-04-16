package com.simplyteam.simplybackup.presentation.viewmodels.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.receiver.BackupReceiver
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import com.simplyteam.simplybackup.data.utils.ActivityUtil.StartActivityWithAnimation
import com.simplyteam.simplybackup.presentation.activities.ConnectionConfigurationActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _connectionSearchService: ConnectionSearchService,
    private val _schedulerService: SchedulerService
) : ViewModel() {

    val ListState = LazyListState()
    val SnackbarHostState = SnackbarHostState()

    fun GetConnections() = _connectionSearchService.FilteredItems

    fun GetSearchText() = _connectionSearchService.GetSearchText()

    fun Search(searchText: String) {
        _connectionSearchService.Search(searchText)
    }

    fun ResetSearch() {
        _connectionSearchService.Search("")
    }

    fun DeleteConnection(
        context: Context,
        connection: Connection
    ) {
        try {
            viewModelScope.launch {
                _connectionRepository.UpdateConnection(
                    connection.apply {
                        TemporarilyDeleted = true
                    }
                )

                val result = SnackbarHostState.showSnackbar(
                    context.getString(
                        R.string.ConnectionRemoved,
                        connection.Name
                    ),
                    context.getString(
                        R.string.Undo
                    )
                )

                if (result == SnackbarResult.Dismissed) {
                    _connectionRepository.RemoveConnection(connection)

                    _schedulerService.CancelBackup(connection)
                } else {
                    _connectionRepository.UpdateConnection(
                        connection.apply {
                            TemporarilyDeleted = false
                        }
                    )
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun StartConfiguration(
        context: ComponentActivity,
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

        context.StartActivityWithAnimation(
            intent
        )
    }

    suspend fun RunBackup(
        context: Context,
        connection: Connection
    ) {
        try {
            val intent = CreateIntent(
                context,
                connection
            )

            context.sendBroadcast(intent)

            SnackbarHostState.showSnackbar(
                context.getString(
                    R.string.BackupStarted,
                    connection.Name
                )
            )
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun CreateIntent(
        context: Context,
        connection: Connection
    ): Intent {
        val intent = Intent(
            context,
            BackupReceiver::class.java
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

        return intent
    }
}