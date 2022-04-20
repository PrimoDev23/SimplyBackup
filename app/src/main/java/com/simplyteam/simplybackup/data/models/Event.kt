package com.simplyteam.simplybackup.data.models

import android.content.Intent

sealed class Event {
    data class ConnectionRemovedEvent(val text: UIText, val action: UIText, val connection: Connection) : Event()
    data class SimpleTextEvent(val text: UIText) : Event()
    data class GoogleSignInEvent(val Intent: Intent) : Event()
}
