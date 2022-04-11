package com.simplyteam.simplybackup.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.views.main.ConnectionOverviewView
import com.simplyteam.simplybackup.presentation.views.main.HomeView
import com.simplyteam.simplybackup.presentation.views.main.SettingsView

@Composable
fun MainNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.History.Route
    ) {
        composable(Screen.History.Route) {
            HomeView(paddingValues)
        }

        composable(Screen.Connections.Route) {
            ConnectionOverviewView(paddingValues)
        }

        composable(Screen.Settings.Route) {
            SettingsView(paddingValues)
        }
    }
}