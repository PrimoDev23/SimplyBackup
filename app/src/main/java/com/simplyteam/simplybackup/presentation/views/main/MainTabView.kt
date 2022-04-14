package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel

@Composable
fun MainTabView() {
    val navController = rememberNavController()
    val currentScreen = remember {
        mutableStateOf<Screen>(Screen.History)
    }

    val historyViewModel = hiltViewModel<HistoryViewModel>()
    val overviewViewModel = hiltViewModel<ConnectionOverviewViewModel>()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                historyViewModel = historyViewModel,
                overviewViewModel = overviewViewModel
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                currentScreen = currentScreen
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .testTag("AddConnection"),
                onClick = {
                    overviewViewModel.StartConfiguration(
                        context,
                        null
                    )
                },
                backgroundColor = MaterialTheme.colors.primaryVariant
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.ConfigureConnection)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        MainNavigation(
            navController = navController,
            paddingValues = it,
            historyViewModel = historyViewModel,
            overviewViewModel = overviewViewModel
        )
    }
}

@Composable
private fun TopBar(
    currentScreen: MutableState<Screen>,
    historyViewModel: HistoryViewModel,
    overviewViewModel: ConnectionOverviewViewModel
) {
    val currentLazyListState = when (currentScreen.value) {
        Screen.History -> {
            historyViewModel.ListState
        }
        Screen.Connections -> {
            overviewViewModel.ListState
        }
        else -> {
            throw Exception("Undefined LazyListState")
        }
    }

    val elevation by animateDpAsState(
        if (currentLazyListState.firstVisibleItemIndex == 0) {
            minOf(
                currentLazyListState.firstVisibleItemScrollOffset.toFloat().dp,
                AppBarDefaults.TopAppBarElevation
            )
        } else {
            AppBarDefaults.TopAppBarElevation
        }
    )

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = currentScreen.value.Title
                )
            )
        },
        elevation = elevation,
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun BottomBar(
    navController: NavController,
    currentScreen: MutableState<Screen>
) {
    val items = listOf(
        Screen.History,
        Screen.Connections,
        //Screen.Settings
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomNavigation {
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
                    currentScreen.value = item
                })
        }
    }
}