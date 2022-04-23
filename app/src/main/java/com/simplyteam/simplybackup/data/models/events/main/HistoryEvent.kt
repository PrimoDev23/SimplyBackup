package com.simplyteam.simplybackup.data.models.events.main

import com.simplyteam.simplybackup.data.models.Connection

sealed class HistoryEvent {
    data class OnOpenHistory(val Connection: Connection): HistoryEvent()
    data class Search(val Value: String): HistoryEvent()
    object ResetSearch: HistoryEvent()
}