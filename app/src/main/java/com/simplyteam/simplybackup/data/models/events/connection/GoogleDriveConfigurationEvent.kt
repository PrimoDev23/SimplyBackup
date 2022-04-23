package com.simplyteam.simplybackup.data.models.events.connection

sealed class GoogleDriveConfigurationEvent {
    data class OnNameChange(val Value: String) : GoogleDriveConfigurationEvent()
    data class OnFolderLinkChange(val Value: String) : GoogleDriveConfigurationEvent()
    data class OnSelectedAccountChange(val Value: String) : GoogleDriveConfigurationEvent()
    object OnLoginCardClicked : GoogleDriveConfigurationEvent()
    object OnDialogDismissed : GoogleDriveConfigurationEvent()
    object OnRequestSignIn: GoogleDriveConfigurationEvent()
}