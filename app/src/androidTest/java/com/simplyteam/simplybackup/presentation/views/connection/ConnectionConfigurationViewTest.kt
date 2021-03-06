package com.simplyteam.simplybackup.presentation.views.connection

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.simplyteam.simplybackup.BuildConfig
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.TestConstants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.data.models.events.connection.ConnectionConfigurationEvent
import com.simplyteam.simplybackup.data.models.events.connection.PathsConfigurationEvent
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.utils.ActivityUtil.FinishActivityWithAnimation
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.connection.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
@UninstallModules(AppModule::class)
class ConnectionConfigurationViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var SchedulerService: SchedulerService

    @Inject
    lateinit var GoogleDriveService: GoogleDriveService

    @Inject
    lateinit var AccountRepository: AccountRepository

    private lateinit var _connection: Connection
    private lateinit var _viewModel: ConnectionConfigurationViewModel

    @OptIn(ExperimentalAnimationApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()

        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(InstrumentationRegistry.getInstrumentation().targetContext, config)

        _viewModel = ConnectionConfigurationViewModel(
            ConnectionRepository,
            SchedulerService
        )

        val nextCloudViewModel = NextCloudConfigurationViewModel()
        val ftpViewModel = SFTPConfigurationViewModel()
        val googleDriveViewModel = GoogleDriveConfigurationViewModel(
            AccountRepository,
            GoogleDriveService
        )
        val seaFileViewModel = SeaFileConfigurationViewModel()

        _viewModel.ViewModelMap[ConnectionType.NextCloud] =
            nextCloudViewModel
        _viewModel.ViewModelMap[ConnectionType.SFTP] =
            ftpViewModel
        _viewModel.ViewModelMap[ConnectionType.GoogleDrive] =
            googleDriveViewModel
        _viewModel.ViewModelMap[ConnectionType.SeaFile] =
            seaFileViewModel

        composeRule.setContent {
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
                    googleDriveViewModel.NewAccountFlow.collect {
                        googleDriveLoginLauncher.launch(it)
                    }
                }

                LaunchedEffect(true){
                    _viewModel.ConfigurePathsFlow.collect {
                        pathsConfigurationViewModel.OnEvent(PathsConfigurationEvent.OnLoadData(_viewModel.Paths))
                        navController.navigate(Screen.PathsConfiguration.Route)
                    }
                }

                LaunchedEffect(true) {
                    pathsConfigurationViewModel.SaveFlow.collect {
                        _viewModel.Paths = it
                        navController.popBackStack()
                    }
                }

                LaunchedEffect(key1 = true) {
                    _viewModel.FinishFlow.collect {
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
                    if (currentScreen == Screen.ConnectionConfiguration && _viewModel.ScrollState.value != 0) {
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
                                    _viewModel.Paths =
                                        pathsConfigurationViewModel.State.Paths
                                    navController.popBackStack()
                                }
                            },
                            elevation = elevation
                        )
                    }
                ) {
                    ConnectionConfigurationNavigation(
                        navController = navController,
                        paddingValues = it,
                        connectionConfigurationViewModel = _viewModel,
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

    @Test
    fun CreateConnection() {
        val connections = runBlocking {
            ConnectionRepository.GetAllConnections()
        }

        val testValue = "TestEntry"

        if (TestConstants.TestConnectionType != ConnectionType.NextCloud) {
            composeRule.onNodeWithTag(TestConstants.TestConnectionType.name)
                .performClick()
        }

        composeRule.onNodeWithTag("Name")
            .performTextInput(testValue)

        when (TestConstants.TestConnectionType) {
            ConnectionType.GoogleDrive -> {
                composeRule.onNodeWithTag("LoginCard")
                    .performClick()

                composeRule.onNodeWithTag("AccountSelectionCard")
                    .assertExists()

                composeRule.onNodeWithTag("${BuildConfig.GOOGLE_DRIVE_USER}Radio")
                    .performClick()

                composeRule.onNodeWithTag("OK")
                    .performClick()
            }
            else -> {
                composeRule.onNodeWithTag("Username")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("Host")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("Password")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("RemotePath")
                    .performTextReplacement("/")

                if(TestConstants.TestConnectionType == ConnectionType.SeaFile){
                    composeRule.onNodeWithTag("RepoId")
                        .performTextReplacement(testValue)
                }
            }
        }

        composeRule.onNodeWithTag("ConfigurePaths")
            .performClick()

        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Pictures")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithText("/sdcard/Pictures")
            .assertExists()

        composeRule.onNodeWithTag("Save")
            .performClick()

        composeRule.onNodeWithTag("WifiOnly")
            .performClick()

        composeRule.onNodeWithTag("ScheduleTypeCard")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithTag("MonthlyMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performScrollTo()
            .performClick()

        val newConnections = runBlocking {
            ConnectionRepository.GetAllConnections()
        }

        assertEquals(
            connections.size + 1,
            newConnections.size
        )

        val newConnection = newConnections.last()

        assertEquals(
            TestConstants.TestConnectionType,
            newConnection.ConnectionType
        )
        assertEquals(
            testValue,
            newConnection.Name
        )

        when (TestConstants.TestConnectionType) {
            ConnectionType.GoogleDrive -> {
                assertEquals(
                    BuildConfig.GOOGLE_DRIVE_USER,
                    newConnection.Username
                )
            }
            else -> {
                assertEquals(
                    testValue,
                    newConnection.Host
                )
                assertEquals(
                    testValue,
                    newConnection.Username
                )
                assertEquals(
                    testValue,
                    newConnection.Password
                )
                assertEquals(
                    "/",
                    newConnection.RemotePath
                )

                if(TestConstants.TestConnectionType == ConnectionType.SeaFile){
                    assertEquals(
                        testValue,
                        newConnection.RepoId
                    )
                }
            }
        }

        assert(newConnection.WifiOnly)
        assertEquals(
            ScheduleType.MONTHLY,
            newConnection.ScheduleType
        )
        assert(newConnection.Paths.isNotEmpty())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun EditConnection() {
        _connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        runBlocking {
            _viewModel.OnEvent(
                ConnectionConfigurationEvent.OnLoadData(
                    _connection
                )
            )
        }

        val testValue = "TestValue"

        composeRule.onNodeWithTag("Name")
            .performTextReplacement(testValue)

        when (TestConstants.TestConnectionType) {
            ConnectionType.GoogleDrive -> {
                composeRule.onNodeWithTag("LoginCard")
                    .performClick()

                composeRule.onNodeWithTag("AccountSelectionCard")
                    .assertExists()

                composeRule.onNodeWithTag("${BuildConfig.GOOGLE_DRIVE_EDITUSER}Radio")
                    .performClick()

                composeRule.onNodeWithTag("OK")
                    .performClick()
            }
            else -> {
                composeRule.onNodeWithTag("Username")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("Host")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("Password")
                    .performTextReplacement(testValue)

                composeRule.onNodeWithTag("RemotePath")
                    .performTextReplacement("/")

                if(TestConstants.TestConnectionType == ConnectionType.SeaFile){
                    composeRule.onNodeWithTag("RepoId")
                        .performTextReplacement(testValue)
                }
            }
        }

        composeRule.onNodeWithTag("ConfigurePaths")
            .performClick()

        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Movies")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithText("/sdcard/Movies")
            .assertExists()

        composeRule.onNodeWithTag("Save")
            .performClick()

        composeRule.onNodeWithTag("WifiOnly")
            .performClick()

        composeRule.onNodeWithTag("ScheduleTypeCard")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithTag("YearlyMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performScrollTo()
            .performClick()

        val connection = runBlocking {
            ConnectionRepository.GetAllConnections()
                .first {
                    it.Id == _connection.Id
                }
        }

        assertEquals(
            testValue,
            connection.Name
        )

        when (TestConstants.TestConnectionType) {
            ConnectionType.GoogleDrive -> {
                assertEquals(
                    BuildConfig.GOOGLE_DRIVE_EDITUSER,
                    connection.Username
                )
            }
            else -> {
                assertEquals(
                    testValue,
                    connection.Host
                )
                assertEquals(
                    testValue,
                    connection.Username
                )
                assertEquals(
                    testValue,
                    connection.Password
                )
                assertEquals(
                    "/",
                    connection.RemotePath
                )
                if(TestConstants.TestConnectionType == ConnectionType.SeaFile) {
                    assertEquals(
                        testValue,
                        connection.RepoId
                    )
                }
            }
        }

        assert(connection.WifiOnly)
        assertEquals(
            ScheduleType.YEARLY,
            connection.ScheduleType
        )
        assert(connection.Paths.isNotEmpty())
    }
}