package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ConnectionOverviewViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var ConnectionSearchService: ConnectionSearchService

    @Inject
    lateinit var SchedulerService: SchedulerService

    @Inject
    lateinit var NextCloudService: NextCloudService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun ConnectionSearchTest() {
        val connection = ConnectionUtil.InsertConnection(
            ConnectionType.NextCloud,
            ConnectionRepository
        )

        val viewModel = ConnectionOverviewViewModel(
            ConnectionRepository,
            ConnectionSearchService,
            SchedulerService
        )

        composeRule.setContent {
            LaunchedEffect(true) {
                ConnectionRepository.GetFlow()
                    .collect {
                        ConnectionRepository.Connections = it

                        ConnectionSearchService.RepeatSearch()
                    }
            }

            SimplyBackupTheme {
                ConnectionOverviewView(
                    paddingValues = PaddingValues(0.dp),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithTag("SearchField")
            .performTextInput("Android")

        composeRule.onNodeWithText(connection.Name)
            .assertExists()

        composeRule.onNodeWithTag("SearchField")
            .performTextReplacement("1234")

        composeRule.onNodeWithText(connection.Name)
            .assertDoesNotExist()

        composeRule.onNodeWithTag("SearchField")
            .performTextClearance()

        composeRule.onNodeWithText(connection.Name)
            .assertExists()

        composeRule.onNodeWithTag("SearchField")
            .performTextReplacement("1234")

        composeRule.onNodeWithText(connection.Name)
            .assertDoesNotExist()

        composeRule.onNodeWithTag("ClearSearch")
            .performClick()

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun AddConnectionTest() {
        val viewModel = ConnectionOverviewViewModel(
            ConnectionRepository,
            ConnectionSearchService,
            SchedulerService
        )

        composeRule.setContent {
            LaunchedEffect(true) {
                ConnectionRepository.GetFlow()
                    .collect {
                        ConnectionRepository.Connections = it

                        ConnectionSearchService.RepeatSearch()
                    }
            }

            SimplyBackupTheme {
                ConnectionOverviewView(
                    paddingValues = PaddingValues(0.dp),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertDoesNotExist()

        val connection = ConnectionUtil.InsertConnection(
            ConnectionType.NextCloud,
            ConnectionRepository
        )

        Thread.sleep(2000)

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun DeleteConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(
            ConnectionType.NextCloud,
            ConnectionRepository
        )

        val viewModel = ConnectionOverviewViewModel(
            ConnectionRepository,
            ConnectionSearchService,
            SchedulerService
        )

        composeRule.setContent {
            val scaffoldState = rememberScaffoldState()

            val context = LocalContext.current

            LaunchedEffect(true) {
                ConnectionRepository.GetFlow()
                    .collect {
                        ConnectionRepository.Connections = it

                        ConnectionSearchService.RepeatSearch()
                    }
            }

            LaunchedEffect(true) {
                viewModel.ConnectionRemovedFlow.collect {
                    when (scaffoldState.snackbarHostState.showSnackbar(
                        it.text.asString(context),
                        it.action.asString(context)
                    )) {
                        SnackbarResult.ActionPerformed -> {
                            viewModel.RestoreConnection(it.connection)
                        }
                        SnackbarResult.Dismissed -> {
                            viewModel.FinishConnectionRemoval(it.connection)
                        }
                    }
                }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = {
                    SnackbarHost(
                        modifier = Modifier
                            .testTag("Snackbar"),
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
                Column(
                    modifier = Modifier
                        .padding(it)
                ) {
                    ConnectionOverviewView(
                        paddingValues = PaddingValues(0.dp),
                        viewModel = viewModel
                    )
                }
            }
        }

        composeRule.onNodeWithText(connection.Name)
            .assertExists()

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Snackbar")
            .assertExists()

        composeRule.onNodeWithText("Undo")
            .assertExists()

        composeRule.onNodeWithText(connection.Name)
            .assertDoesNotExist()

        composeRule.onNodeWithText("Undo")
            .performClick()

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun BackupConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(
            ConnectionType.NextCloud,
            ConnectionRepository
        )

        val viewModel = ConnectionOverviewViewModel(
            ConnectionRepository,
            ConnectionSearchService,
            SchedulerService
        )

        composeRule.setContent {
            val scaffoldState = rememberScaffoldState()

            val context = LocalContext.current

            LaunchedEffect(true) {
                ConnectionRepository.GetFlow()
                    .collect {
                        ConnectionRepository.Connections = it

                        ConnectionSearchService.RepeatSearch()
                    }
            }

            LaunchedEffect(true) {
                viewModel.BackupStartedFlow.collect {
                    scaffoldState.snackbarHostState.showSnackbar(
                        it.text.asString(context)
                    )
                }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = {
                    SnackbarHost(
                        modifier = Modifier
                            .testTag("Snackbar"),
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
                Column(
                    modifier = Modifier
                        .padding(it)
                ) {
                    ConnectionOverviewView(
                        paddingValues = PaddingValues(0.dp),
                        viewModel = viewModel
                    )
                }
            }
        }

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("BackupMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Snackbar")
            .assertExists()

        Thread.sleep(10000)

        val files = runBlocking {
            NextCloudService.GetFilesForConnection(connection)
        }

        assertEquals(1, files.size)

        runBlocking {
            NextCloudService.DeleteFile(
                connection,
                files[0].RemotePath
            )
        }
    }
}