package com.simplyteam.simplybackup.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.connection.ConnectionConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.PathsConfigurationView

class ConnectionConfigurationNavigation constructor(
    private val connectionConfigurationView: ConnectionConfigurationView,
    private val pathsConfigurationView: PathsConfigurationView
) {

    @Composable
    fun Build(navController: NavHostController, paddingValues: PaddingValues, viewModel: ConnectionConfigurationViewModel) {
        NavHost(
            navController = navController,
            startDestination = Screen.ConnectionConfiguration.Route
        ) {
            composable(Screen.ConnectionConfiguration.Route) {
                connectionConfigurationView.Build(paddingValues, navController, viewModel)
            }

            composable(Screen.PathsConfiguration.Route) {
                pathsConfigurationView.Build(paddingValues, viewModel)
            }
        }
    }

}