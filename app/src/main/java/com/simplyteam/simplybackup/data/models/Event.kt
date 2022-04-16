package com.simplyteam.simplybackup.data.models

sealed class Event {
    data class ConnectionRemovedEvent(val text: UIText, val action: UIText, val connection: Connection) : Event()
    data class SimpleTextEvent(val text: UIText) : Event()
}
