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

@Composable
fun ConnectionConfigurationNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ConnectionConfigurationViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ConnectionConfiguration.Route
    ) {
        composable(Screen.ConnectionConfiguration.Route) {
            ConnectionConfigurationView(
                paddingValues = paddingValues,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.PathsConfiguration.Route) {
            PathsConfigurationView(
                paddingValues,
                viewModel
            )
        }
    }
}