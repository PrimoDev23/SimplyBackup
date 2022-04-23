package com.simplyteam.simplybackup.presentation.uistates.connection

import com.simplyteam.simplybackup.data.models.Path

data class PathsConfigurationState(
    val CurrentPath: String = "",
    val CurrentPathError: Boolean = false,
    val Paths: List<Path> = listOf()
)
