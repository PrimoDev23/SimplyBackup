package com.simplyteam.simplybackup.presentation.views.main

import android.content.Intent
import android.os.Bundle
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.TestWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.TestConstants
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.receiver.RunBackupReceiver
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import com.simplyteam.simplybackup.data.utils.CloudServiceUtil
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import com.simplyteam.simplybackup.data.workers.BackupWorker
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
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
class ConnectionOverviewViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var ConnectionSearchService: ConnectionSearchService

    @Inject
    lateinit var SchedulerService: SchedulerService

    @Inject
    lateinit var NextCloudService: NextCloudService

    @Inject
    lateinit var SFTPService: SFTPService

    @Inject
    lateinit var GoogleDriveService: GoogleDriveService

    @Before
    fun setUp() {
        hiltRule.inject()

        val viewModel = ConnectionOverviewViewModel(
            ConnectionRepository,
            ConnectionSearchService,
            SchedulerService
        )

        composeRule.setContent {
            val scaffoldState = rememberScaffoldState()

            val context = LocalContext.current

            LaunchedEffect(true) {
                viewModel.ConnectionRemovedFlow.collect {
                    when (scaffoldState.snackbarHostState.showSnackbar(
                        it.Message.asString(context),
                        it.Action?.asString(context)
                    )) {
                        SnackbarResult.ActionPerformed -> it.ActionClicked?.invoke()
                        SnackbarResult.Dismissed -> it.Dismissed?.invoke()
                    }
                }
            }

            LaunchedEffect(key1 = true) {
                viewModel.RunBackupFlow.collect {
                    val worker = TestListenableWorkerBuilder<BackupWorker>(
                        ApplicationProvider.getApplicationContext(),
                        Data.Builder()
                            .putLong("ConnectionId", it.Connection.Id)
                            .build()
                    )
                        .setWorkerFactory(workerFactory)
                        .build()

                    worker.doWork()

                    scaffoldState.snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.BackupStarted,
                            it.Connection.Name
                        )
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
                ConnectionOverviewView(
                    paddingValues = it,
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun ConnectionSearchTest() {
        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

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
        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertDoesNotExist()

        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun DeleteConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

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
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("BackupMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Snackbar")
            .assertExists()

        Thread.sleep(10000)

        val files = runBlocking {
            CloudServiceUtil.GetFilesForConnection(
                connection,
                NextCloudService,
                SFTPService,
                GoogleDriveService
            )
        }

        assertEquals(
            1,
            files.size
        )

        runBlocking {
            CloudServiceUtil.CleanupServer(
                connection,
                NextCloudService,
                SFTPService,
                GoogleDriveService
            )
        }
    }
}