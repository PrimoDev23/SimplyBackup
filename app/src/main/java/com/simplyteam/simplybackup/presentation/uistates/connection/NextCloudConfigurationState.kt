package com.simplyteam.simplybackup.presentation.uistates.connection

data class NextCloudConfigurationState(
    val Name: String = "",
    val NameError: Boolean = false,
    val Host: String = "",
    val HostError: Boolean = false,
    val Username: String = "",
    val UsernameError: Boolean = false,
    val Password: String = "",
    val PasswordError: Boolean = false,
    val RemotePath: String = ""
)