package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.simplyteam.simplybackup.presentation.activities.ui.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectionConfigurationActivity : ComponentActivity() {

    @Inject
    lateinit var ConnectionConfigurationNavigation: ConnectionConfigurationNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = this.intent.extras?.get("Connection") as Connection?

        setContent {
            SimplyBackupTheme {
                val navController = rememberNavController()
                val viewModel = hiltViewModel<ConnectionConfigurationViewModel>()
                val nextCloudViewModel = hiltViewModel<NextCloudConfigurationViewModel>()

                viewModel.ViewModelMap[ConnectionType.NextCloud] = nextCloudViewModel

                LaunchedEffect(key1 = true){
                    connection?.let {
                        viewModel.LoadData(connection)
                    }
                }

                Scaffold(
                    topBar = {
                        BuildTopBar(
                            navController = navController
                        )
                    }) {
                    ConnectionConfigurationNavigation.Build(navController, it, viewModel)
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar(navController: NavHostController) {
        val activity = LocalContext.current as ComponentActivity

        TopAppBar(
            title = {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                var resId = Screen.ConnectionConfiguration.Title

                if(currentRoute == Screen.PathsConfiguration.Route){
                    resId = Screen.PathsConfiguration.Title
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
                    val navBackStackEntry = navController.currentBackStackEntry
                    val currentRoute = navBackStackEntry?.destination?.route

                    if(currentRoute == Screen.ConnectionConfiguration.Route) {
                        activity.finish()
                    }else if(currentRoute == Screen.PathsConfiguration.Route){
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            elevation = 4.dp
        )
    }
}