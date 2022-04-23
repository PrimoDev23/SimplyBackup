package com.simplyteam.simplybackup.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.main.AccountOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel
import com.simplyteam.simplybackup.presentation.views.main.AccountOverview
import com.simplyteam.simplybackup.presentation.views.main.ConnectionOverviewView
import com.simplyteam.simplybackup.presentation.views.main.HistoryView

@Composable
fun MainNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    historyViewModel: HistoryViewModel,
    overviewViewModel: ConnectionOverviewViewModel,
    accountOverviewViewModel: AccountOverviewViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.History.Route
    ) {
        composable(Screen.History.Route) {
            HistoryView(
                paddingValues = paddingValues,
                viewModel = historyViewModel
            )
        }

        composable(Screen.Connections.Route) {
            ConnectionOverviewView(
                paddingValues = paddingValues,
                viewModel = overviewViewModel
            )
        }

        composable(Screen.Accounts.Route) {
            AccountOverview(
                paddingValues = paddingValues,
                viewModel = accountOverviewViewModel
            )
        }
    }
}