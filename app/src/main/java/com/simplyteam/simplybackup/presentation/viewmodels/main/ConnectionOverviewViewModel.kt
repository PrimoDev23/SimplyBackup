package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.presentation.views.IconProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectionOverviewViewModel @Inject constructor(
    val connectionRepository: ConnectionRepository,
    val IconProvider: IconProvider
) : ViewModel() {

    fun GetConnections() = connectionRepository.Connections.value

    fun GetIconProvider() = IconProvider

    suspend fun DeleteConnection(entry: Connection) = connectionRepository.RemoveConnection(entry)
}