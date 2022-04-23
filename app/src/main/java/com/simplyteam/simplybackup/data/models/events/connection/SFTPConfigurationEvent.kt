package com.simplyteam.simplybackup.data.models.events.connection

sealed class SFTPConfigurationEvent {
    data class OnNameChange(val Value: String) : SFTPConfigurationEvent()
    data class OnHostChange(val Value: String) : SFTPConfigurationEvent()
    data class OnUsernameChange(val Value: String) : SFTPConfigurationEvent()
    data class OnPasswordChange(val Value: String) : SFTPConfigurationEvent()
    data class OnRemotePathChange(val Value: String) : SFTPConfigurationEvent()
}