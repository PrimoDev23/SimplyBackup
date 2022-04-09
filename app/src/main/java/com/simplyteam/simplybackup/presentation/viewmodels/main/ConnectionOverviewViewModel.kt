package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.presentation.views.IconProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _iconProvider: IconProvider
) : ViewModel() {

    fun GetConnections() = _connectionRepository.Connections.value

    fun GetIconProvider() = _iconProvider

    suspend fun DeleteConnection(entry: Connection) {
        try {
            _connectionRepository.RemoveConnection(entry)
        }catch (ex: Exception){
            Timber.e(ex)
        }
    }
}