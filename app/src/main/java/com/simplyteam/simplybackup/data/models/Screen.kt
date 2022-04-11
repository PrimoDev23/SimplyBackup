package com.simplyteam.simplybackup.data.models

import com.simplyteam.simplybackup.R

sealed class Screen(
    val Route: String,
    val Title: Int,
    val Icon: Int
) {

    //region Main

    object Home : Screen(
        Route = "home",
        Title = R.string.Home,
        Icon = R.drawable.ic_baseline_home_24
    )
    object Connections : Screen(
        Route = "connections",
        Title = R.string.Connections,
        Icon = R.drawable.ic_baseline_cloud_24
    )
    object Settings : Screen(
        Route = "settings",
        Title = R.string.Settings,
        Icon = R.drawable.ic_baseline_settings_24
    )

    //endregion

    //region ConnectionConfiguration

    object ConnectionConfiguration : Screen(
        Route = "configureConnection",
        Title = R.string.ConfigureConnection,
        Icon = R.drawable.ic_baseline_cloud_24
    )

    object PathsConfiguration : Screen(
        Route = "pathsConfiguration",
        Title = R.string.ConfigurePaths,
        Icon = R.drawable.ic_baseline_folder_24
    )

    //endregion

}
