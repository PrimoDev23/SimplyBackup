package com.simplyteam.simplybackup.data.models.events.main

import com.simplyteam.simplybackup.data.models.Connection

sealed class ConnectionOverviewEvent {
    data class OnDeleteConnection(val Connection: Connection): ConnectionOverviewEvent()
    data class OnBackupConnection(val Connection: Connection): ConnectionOverviewEvent()
    data class Search(val Value: String): ConnectionOverviewEvent()
    object ResetSearch: ConnectionOverviewEvent()
}
