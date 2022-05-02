package com.simplyteam.simplybackup.presentation.uistates.connection

import com.simplyteam.simplybackup.R

data class SeaFileConfigurationState(
    val Name: String = "",
    val NameError: Boolean = false,
    val Host: String = "",
    val HostError: Boolean = false,
    val Username: String = "",
    val UsernameError: Boolean = false,
    val Password: String = "",
    val PasswordError: Boolean = false,
    val RepoId: String = "",
    val RepoIdError: Boolean = false,
    val RemotePath: String = "",
    val RemotePathError: Int = R.string.PlaceholderValue
)