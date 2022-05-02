package com.simplyteam.simplybackup.data.models.events.connection

sealed class SeaFileConfigurationEvent {
    data class OnNameChange(val Value: String) : SeaFileConfigurationEvent()
    data class OnHostChange(val Value: String) : SeaFileConfigurationEvent()
    data class OnUsernameChange(val Value: String) : SeaFileConfigurationEvent()
    data class OnPasswordChange(val Value: String) : SeaFileConfigurationEvent()
    data class OnRepoIdChange(val Value: String) : SeaFileConfigurationEvent()
    data class OnRemotePathChange(val Value: String) : SeaFileConfigurationEvent()
}