package com.simplyteam.simplybackup.presentation.uistates.connection

import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType

data class ConnectionConfigurationState(
    val SelectedConnectionType: ConnectionType = ConnectionType.NextCloud,
    val BackupPassword: String = "",
    val WifiOnly: Boolean = false,
    val SelectedScheduleType: ScheduleType = ScheduleType.DAILY
)
