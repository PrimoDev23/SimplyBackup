package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.data.models.events.connection.ConnectionConfigurationEvent
import com.simplyteam.simplybackup.data.models.events.connection.PathsConfigurationEvent
import com.simplyteam.simplybackup.data.utils.ActivityUtil.FinishActivityWithAnimation
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.connection.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectionConfigurationActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = this.intent.extras?.get("Connection") as Connection?

        val connectionConfigurationViewModel = ViewModelProvider(this).get(ConnectionConfigurationViewModel::class.java)
        val nextCloudViewModel = ViewModelProvider(this).get(NextCloudConfigurationViewModel::class.java)
        val ftpViewModel = ViewModelProvider(this).get(SFTPConfigurationViewModel::class.java)
        val googleDriveViewModel = ViewModelProvider(this).get(GoogleDriveConfigurationViewModel::class.java)
        val seaFileViewModel = ViewModelProvider(this).get(SeaFileConfigurationViewModel::class.java)

        connectionConfigurationViewModel.ViewModelMap[ConnectionType.NextCloud] =
            nextCloudViewModel
        connectionConfigurationViewModel.ViewModelMap[ConnectionType.SFTP] =
            ftpViewModel
        connectionConfigurationViewModel.ViewModelMap[ConnectionType.GoogleDrive] =
            googleDriveViewModel
        connectionConfigurationViewModel.ViewModelMap[ConnectionType.SeaFile] =
            seaFileViewModel

        setContent {
            SimplyBackupTheme {
                val context = LocalContext.current as ComponentActivity

                val navController = rememberAnimatedNavController()

                val pathsConfigurationViewModel = viewModel<PathsConfigurationViewModel>()

                val googleDriveLoginLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) {
                    it.data?.let { data ->
                        googleDriveViewModel.SetAccountFromIntent(data)
                    }
                }

                LaunchedEffect(key1 = true) {
                    connection?.let {
                        connectionConfigurationViewModel.OnEvent(ConnectionConfigurationEvent.OnLoadData(connection))
                        pathsConfigurationViewModel.OnEvent(PathsConfigurationEvent.OnLoadData(connection.Paths))
                    }
                }

                LaunchedEffect(key1 = true) {
                    googleDriveViewModel.NewAccountFlow.collect {
                        googleDriveLoginLauncher.launch(it)
                    }
                }

                LaunchedEffect(key1 = true) {
                    connectionConfigurationViewModel.FinishFlow.collect {
                        context.FinishActivityWithAnimation()
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var currentScreen by remember {
                    mutableStateOf<Screen>(Screen.ConnectionConfiguration)
                }

                LaunchedEffect(key1 = navBackStackEntry) {
                    currentScreen = when (navBackStackEntry?.destination?.route) {
                        Screen.ConnectionConfiguration.Route -> {
                            Screen.ConnectionConfiguration
                        }
                        else -> {
                            Screen.PathsConfiguration
                        }
                    }
                }

                val elevation by animateDpAsState(
                    if (currentScreen == Screen.ConnectionConfiguration && connectionConfigurationViewModel.ScrollState.value != 0) {
                        AppBarDefaults.TopAppBarElevation
                    } else {
                        0.dp
                    }
                )

                Scaffold(
                    topBar = {
                        BuildTopBar(
                            title = stringResource(
                                id = currentScreen.Title
                            ),
                            onBackPressed = {
                                if (currentScreen == Screen.ConnectionConfiguration) {
                                    context.FinishActivityWithAnimation()
                                } else if (currentScreen == Screen.PathsConfiguration) {
                                    connectionConfigurationViewModel.Paths =
                                        pathsConfigurationViewModel.State.Paths
                                    navController.popBackStack()
                                }
                            },
                            elevation = elevation
                        )
                    }) {
                    ConnectionConfigurationNavigation(
                        navController = navController,
                        paddingValues = it,
                        connectionConfigurationViewModel = connectionConfigurationViewModel,
                        pathsConfigurationViewModel = pathsConfigurationViewModel
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar(
        title: String,
        onBackPressed: () -> Unit,
        elevation: Dp
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title
                )
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .testTag("BackButton"),
                    onClick = onBackPressed
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            elevation = elevation,
            backgroundColor = MaterialTheme.colors.background
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_right
        )
    }
}