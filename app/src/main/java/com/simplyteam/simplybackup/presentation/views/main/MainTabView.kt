package com.simplyteam.simplybackup.presentation.views.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.data.receiver.BackupReceiver
import com.simplyteam.simplybackup.data.utils.ActivityUtil
import com.simplyteam.simplybackup.data.utils.ActivityUtil.StartActivityWithAnimation
import com.simplyteam.simplybackup.presentation.activities.BackupHistoryActivity
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation
import com.simplyteam.simplybackup.presentation.viewmodels.main.AccountOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel

@Composable
fun MainTabView() {
    val activity = LocalContext.current as ComponentActivity

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.History)
    }

    val historyViewModel = viewModel<HistoryViewModel>()
    val overviewViewModel = viewModel<ConnectionOverviewViewModel>()
    val accountsViewModel = viewModel<AccountOverviewViewModel>()

    SetupEvents(
        overviewViewModel = overviewViewModel,
        historyViewModel = historyViewModel,
        accountOverviewViewModel = accountsViewModel,
        snackbarHostState = scaffoldState.snackbarHostState
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                lazyListState = when (currentScreen) {
                    Screen.History -> {
                        historyViewModel.ListState
                    }
                    Screen.Connections -> {
                        overviewViewModel.ListState
                    }
                    Screen.Accounts -> {
                        accountsViewModel.ListState
                    }
                    else -> {
                        throw Exception("Undefined LazyListState")
                    }
                },
                title = stringResource(
                    id = currentScreen.Title
                )
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                onNavigate = {
                    currentScreen = it
                }
            )
        },
        floatingActionButton = {
            if (currentScreen == Screen.Connections) {
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
            }
        },
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
            overviewViewModel = overviewViewModel,
            accountOverviewViewModel = accountsViewModel
        )
    }
}

@Composable
fun SetupEvents(
    overviewViewModel: ConnectionOverviewViewModel,
    historyViewModel: HistoryViewModel,
    accountOverviewViewModel: AccountOverviewViewModel,
    snackbarHostState: SnackbarHostState
) {
    val activity = LocalContext.current as ComponentActivity

    LaunchedEffect(key1 = true) {
        overviewViewModel.ConnectionRemovedFlow.collect {
            when (snackbarHostState.showSnackbar(
                it.Message.asString(activity),
                it.Action?.asString(activity)
            )) {
                SnackbarResult.Dismissed -> it.Dismissed?.invoke()
                SnackbarResult.ActionPerformed -> it.ActionClicked?.invoke()
            }
        }
    }

    LaunchedEffect(key1 = true) {
        overviewViewModel.RunBackupFlow.collect {
            val intent = Intent(
                activity,
                BackupReceiver::class.java
            )

            val bundle = Bundle()
            bundle.putSerializable(
                "Connection",
                it.Connection
            )
            intent.putExtra(
                "Bundle",
                bundle
            )

            activity.sendBroadcast(intent)

            snackbarHostState.showSnackbar(
                activity.getString(
                    R.string.BackupStarted,
                    it.Connection.Name
                )
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

    LaunchedEffect(key1 = true) {
        accountOverviewViewModel.ConnectionExistsFlow.collect {
            snackbarHostState.showSnackbar(
                it.Message.asString(activity)
            )
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    lazyListState: LazyListState
) {
    val elevation by animateDpAsState(
        if (lazyListState.firstVisibleItemIndex == 0) {
            minOf(
                lazyListState.firstVisibleItemScrollOffset.toFloat().dp,
                AppBarDefaults.TopAppBarElevation
            )
        } else {
            AppBarDefaults.TopAppBarElevation
        }
    )

    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        elevation = elevation,
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun BottomBar(
    navController: NavController,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Screen.History,
        Screen.Connections,
        Screen.Accounts
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

                    onNavigate(item)
                }
            )
        }
    }
}