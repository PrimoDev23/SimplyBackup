package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SFTPConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectionConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = this.intent.extras?.get("Connection") as Connection?

        setContent {
            SimplyBackupTheme {
                val navController = rememberNavController()
                val viewModel = hiltViewModel<ConnectionConfigurationViewModel>()
                val nextCloudViewModel = hiltViewModel<NextCloudConfigurationViewModel>()
                val ftpViewModel = hiltViewModel<SFTPConfigurationViewModel>()

                viewModel.ViewModelMap[ConnectionType.NextCloud] = nextCloudViewModel
                viewModel.ViewModelMap[ConnectionType.SFTP] = ftpViewModel

                LaunchedEffect(key1 = true) {
                    connection?.let {
                        viewModel.LoadData(connection)
                    }
                }

                Scaffold(
                    topBar = {
                        BuildTopBar(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }) {
                    ConnectionConfigurationNavigation(
                        navController = navController,
                        paddingValues = it,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar(
        navController: NavHostController,
        viewModel: ConnectionConfigurationViewModel
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        val currentScreen = when(currentRoute){
            Screen.ConnectionConfiguration.Route -> {
                Screen.ConnectionConfiguration
            }
            else -> {
                Screen.PathsConfiguration
            }
        }

        val elevation by animateDpAsState(
            if(viewModel.ScrollState.value != 0 && currentScreen == Screen.ConnectionConfiguration){
                AppBarDefaults.TopAppBarElevation
            }else{
                0.dp
            }
        )
        val activity = LocalContext.current as ComponentActivity

        TopAppBar(
            title = {


                val resId = if (currentRoute == Screen.PathsConfiguration.Route) {
                    Screen.PathsConfiguration.Title
                }else{
                    Screen.ConnectionConfiguration.Title
                }

                Text(
                    text = stringResource(
                        id = resId
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .testTag("BackButton"),
                    onClick = {
                        if (currentScreen == Screen.ConnectionConfiguration) {
                            activity.finish()
                        } else if (currentScreen == Screen.PathsConfiguration) {
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
}