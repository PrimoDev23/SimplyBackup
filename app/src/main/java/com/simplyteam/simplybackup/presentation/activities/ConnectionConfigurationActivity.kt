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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.Screen
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

        setContent {
            SimplyBackupTheme {
                val context = LocalContext.current as ComponentActivity

                val navController = rememberAnimatedNavController()

                val connectionConfigurationViewModel = viewModel<ConnectionConfigurationViewModel>()
                val pathsConfigurationViewModel = viewModel<PathsConfigurationViewModel>()

                val nextCloudViewModel = viewModel<NextCloudConfigurationViewModel>()
                val ftpViewModel = viewModel<SFTPConfigurationViewModel>()
                val googleDriveViewModel = viewModel<GoogleDriveConfigurationViewModel>()

                val googleDriveLoginLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ){
                    it.data?.let { data ->
                        googleDriveViewModel.SetAccountFromIntent(data)
                    }
                }

                connectionConfigurationViewModel.ViewModelMap[ConnectionType.NextCloud] = nextCloudViewModel
                connectionConfigurationViewModel.ViewModelMap[ConnectionType.SFTP] = ftpViewModel
                connectionConfigurationViewModel.ViewModelMap[ConnectionType.GoogleDrive] = googleDriveViewModel

                LaunchedEffect(key1 = true) {
                    connection?.let {
                        connectionConfigurationViewModel.LoadData(connection)
                    }
                }

                LaunchedEffect(key1 = true){
                    googleDriveViewModel.NewAccountFlow.collect {
                        googleDriveLoginLauncher.launch(it.Intent)
                    }
                }

                LaunchedEffect(key1 = true) {
                    connectionConfigurationViewModel.FinishFlow.collect {
                        context.FinishActivityWithAnimation()
                    }
                }

                Scaffold(
                    topBar = {
                        BuildTopBar(
                            navController = navController,
                            connectionConfigurationViewModel = connectionConfigurationViewModel,
                            pathsConfigurationViewModel = pathsConfigurationViewModel
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
        navController: NavHostController,
        connectionConfigurationViewModel: ConnectionConfigurationViewModel,
        pathsConfigurationViewModel: PathsConfigurationViewModel
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()

        val currentScreen = when (navBackStackEntry.value?.destination?.route) {
            Screen.ConnectionConfiguration.Route -> {
                Screen.ConnectionConfiguration
            }
            else -> {
                Screen.PathsConfiguration
            }
        }

        val elevation by animateDpAsState(
            if (connectionConfigurationViewModel.ScrollState.value != 0 && currentScreen == Screen.ConnectionConfiguration) {
                AppBarDefaults.TopAppBarElevation
            } else {
                0.dp
            }
        )
        val activity = LocalContext.current as ComponentActivity

        TopAppBar(
            title = {
                Text(
                    text = stringResource(
                        id = currentScreen.Title
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .testTag("BackButton"),
                    onClick = {
                        if (currentScreen == Screen.ConnectionConfiguration) {
                            activity.FinishActivityWithAnimation()
                        } else if (currentScreen == Screen.PathsConfiguration) {
                            connectionConfigurationViewModel.Paths = pathsConfigurationViewModel.Paths
                            navController.popBackStack()
                        }
                    }) {
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