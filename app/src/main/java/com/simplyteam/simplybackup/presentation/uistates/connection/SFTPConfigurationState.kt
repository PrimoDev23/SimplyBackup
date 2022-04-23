package com.simplyteam.simplybackup.presentation.uistates.connection

data class SFTPConfigurationState(
    val Name: String = "",
    val NameError: Boolean = false,
    val Host: String = "",
    val HostError: Boolean = false,
    val Username: String = "",
    val UsernameError: Boolean = false,
    val Password: String = "",
    val PasswordError: Boolean = false,
    val RemotePath: String = "",
    val RemotePathError: Boolean = false
)
