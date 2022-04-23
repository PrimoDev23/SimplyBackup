package com.simplyteam.simplybackup.data.models.events.connection

import com.simplyteam.simplybackup.data.models.Path

sealed class PathsConfigurationEvent {
    data class OnCurrentPathChange(val Value: String): PathsConfigurationEvent()
    object OnAddPathClicked: PathsConfigurationEvent()
    data class OnDeletePathClicked(val Path: Path): PathsConfigurationEvent()
}