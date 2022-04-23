package com.simplyteam.simplybackup.data.models.events.connection

sealed class NextCloudConfigurationEvent {
    data class OnNameChange(val Value: String) : NextCloudConfigurationEvent()
    data class OnHostChange(val Value: String) : NextCloudConfigurationEvent()
    data class OnUsernameChange(val Value: String) : NextCloudConfigurationEvent()
    data class OnPasswordChange(val Value: String) : NextCloudConfigurationEvent()
    data class OnRemotePathChange(val Value: String) : NextCloudConfigurationEvent()
}