package com.simplyteam.simplybackup.presentation.views.main

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.data.utils.ActivityUtil
import com.simplyteam.simplybackup.data.utils.ActivityUtil.StartActivityWithAnimation
import com.simplyteam.simplybackup.presentation.activities.BackupHistoryActivity
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel
import kotlinx.coroutines.launch

@Composable
fun MainTabView() {
    val activity = LocalContext.current as ComponentActivity

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val currentScreen = remember {
        mutableStateOf<Screen>(Screen.History)
    }

    val historyViewModel = viewModel<HistoryViewModel>()
    val overviewViewModel = viewModel<ConnectionOverviewViewModel>()

    SetupEvents(
        overviewViewModel = overviewViewModel,
        historyViewModel = historyViewModel,
        snackbarHostState = scaffoldState.snackbarHostState
    )

    Scaffold(
        scaffoldState = scaffoldState,
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
                    ActivityUtil.StartConfigurationActivity(
                        activity,
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
        isFloatingActionButtonDocked = true,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier
                    .testTag("MainSnackbar"),
                hostState = scaffoldState.snackbarHostState,
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        backgroundColor = MaterialTheme.colors.background,
                        contentColor = MaterialTheme.colors.onBackground
                    )
                }
            )
        }
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
fun SetupEvents(
    overviewViewModel: ConnectionOverviewViewModel,
    historyViewModel: HistoryViewModel,
    snackbarHostState: SnackbarHostState
) {
    val activity = LocalContext.current as ComponentActivity

    LaunchedEffect(key1 = true) {
        overviewViewModel.ConnectionRemovedFlow.collect {
            when (snackbarHostState.showSnackbar(
                it.text.asString(activity),
                it.action.asString(activity)
            )) {
                SnackbarResult.Dismissed -> overviewViewModel.FinishConnectionRemoval(it.connection)
                SnackbarResult.ActionPerformed -> overviewViewModel.RestoreConnection(it.connection)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        overviewViewModel.BackupStartedFlow.collect {
            snackbarHostState.showSnackbar(
                it.text.asString(activity)
            )
        }
    }

    LaunchedEffect(key1 = true) {
        historyViewModel.OpenHistoryFlow.collect {
            val intent = Intent(
                activity,
                BackupHistoryActivity::class.java
            )
            intent.putExtra(
                "Connection",
                it
            )

            activity.StartActivityWithAnimation(intent)
        }
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