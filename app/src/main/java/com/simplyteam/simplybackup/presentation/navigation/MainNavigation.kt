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

class MainNavigation(
    private val _homeView: HomeView,
    private val _connectionOverviewView: ConnectionOverviewView,
    private val _settingsView: SettingsView
) {

    @Composable
    fun Build(navController: NavHostController, paddingValues: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.Route
        ) {
            composable(Screen.Home.Route) {
                _homeView.Build(paddingValues)
            }

            composable(Screen.CloudOverview.Route) {
                _connectionOverviewView.Build(paddingValues)
            }

            composable(Screen.Settings.Route) {
                _settingsView.Build(paddingValues)
            }
        }
    }

}