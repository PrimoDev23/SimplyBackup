package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation

class MainTabView constructor(
    private val mainNavigation: MainNavigation
) {

    @Composable
    fun Build() {
        val navController = rememberNavController()

        Scaffold(
            topBar = {
                BuildTopBar()
            },
            bottomBar = {
                BuildBottomBar(
                    navController = navController
                )
            }
        ) {
            mainNavigation.Build(
                navController = navController,
                paddingValues = it
            )
        }
    }

    @Composable
    private fun BuildTopBar() {

    }

    @Composable
    private fun BuildBottomBar(navController: NavController) {
        val items = listOf(
            Screen.Home,
            Screen.CloudOverview,
            Screen.Settings
        )

        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        BottomNavigation(
            elevation = 4.dp
        ) {
            for (item in items) {
                BottomNavigationItem(
                    modifier = Modifier
                        .testTag(item.Route),
                    label = {
                        Text(
                            text = stringResource(
                                id = item.Title
                            )
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.Icon),
                            contentDescription = stringResource(
                                id = item.Title
                            )
                        )
                    },
                    selected = currentRoute == item.Route,
                    onClick = {
                        navController.navigate(item.Route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                    })
            }
        }
    }
}