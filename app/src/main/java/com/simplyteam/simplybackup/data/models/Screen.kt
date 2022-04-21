package com.simplyteam.simplybackup.data.models

import com.simplyteam.simplybackup.R

sealed class Screen(
    val Route: String,
    val Title: Int,
    val Icon: Int
) {

    //region Main

    object History : Screen(
        Route = "history",
        Title = R.string.History,
        Icon = R.drawable.ic_baseline_manage_history_24
    )
    object Connections : Screen(
        Route = "connections",
        Title = R.string.Connections,
        Icon = R.drawable.ic_baseline_cloud_24
    )
    object Accounts : Screen(
        Route = "accounts",
        Title = R.string.Accounts,
        Icon = R.drawable.ic_baseline_person_24
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
