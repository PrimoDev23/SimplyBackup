package com.simplyteam.simplybackup.presentation.viewmodels.main

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import com.simplyteam.simplybackup.data.utils.ActivityUtil.StartActivityWithAnimation
import com.simplyteam.simplybackup.presentation.activities.ConnectionConfigurationActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _connectionSearchService: ConnectionSearchService,
    private val _schedulerService: SchedulerService
) : ViewModel() {

    val ListState = LazyListState()

    fun GetConnections() = _connectionSearchService.FilteredItems

    fun GetSearchText() = _connectionSearchService.GetSearchText()

    fun Search(searchText: String) {
        _connectionSearchService.Search(searchText)
    }

    fun ResetSearch() {
        _connectionSearchService.Search("")
    }

    suspend fun DeleteConnection(connection: Connection) {
        try {
            _connectionRepository.RemoveConnection(connection)

            _schedulerService.CancelBackup(connection)
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
}