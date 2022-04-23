package com.simplyteam.simplybackup.presentation.uistates.connection

data class GoogleDriveConfigurationState(
    val Name: String = "",
    val NameError: Boolean = false,
    val FolderLink: String = "",
    val SelectedAccount: String = "",
    val SelectedAccountError: Boolean = false,
    val SelectionDialogShown: Boolean = false
)
