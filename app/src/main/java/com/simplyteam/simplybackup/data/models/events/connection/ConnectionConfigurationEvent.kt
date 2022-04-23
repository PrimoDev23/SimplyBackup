package com.simplyteam.simplybackup.data.models.events.connection

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType

sealed class ConnectionConfigurationEvent {
    data class OnLoadData(val Connection: Connection) : ConnectionConfigurationEvent()
    data class OnSelectedConnectionTypeChange(val Value: ConnectionType) : ConnectionConfigurationEvent()
    data class OnSelectedScheduleTypeChange(val Value: ScheduleType) : ConnectionConfigurationEvent()
    data class OnBackupPasswordChange(val Value: String) : ConnectionConfigurationEvent()
    data class OnWifiOnlyChange(val Value: Boolean) : ConnectionConfigurationEvent()
    object OnSaveConnection : ConnectionConfigurationEvent()
}
