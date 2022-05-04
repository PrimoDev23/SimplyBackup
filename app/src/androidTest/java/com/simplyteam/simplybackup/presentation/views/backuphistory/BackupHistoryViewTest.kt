package com.simplyteam.simplybackup.presentation.views.backuphistory

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.TestConstants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.cloudservices.seafile.SeaFileService
import com.simplyteam.simplybackup.data.utils.CloudServiceUtil
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import com.simplyteam.simplybackup.data.utils.TestFileUtil
import com.simplyteam.simplybackup.presentation.viewmodels.backuphistory.BackupHistoryViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class BackupHistoryViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var NextCloudService: NextCloudService

    @Inject
    lateinit var SFTPService: SFTPService

    @Inject
    lateinit var GoogleDriveService: GoogleDriveService

    @Inject
    lateinit var SeaFileService: SeaFileService

    @Inject
    lateinit var PackagingService: PackagingService

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    @ApplicationContext
    lateinit var Context: Context

    lateinit var ViewModel: BackupHistoryViewModel

    lateinit var Connection: Connection

    @Before
    fun setUp() {
        hiltRule.inject()

        ViewModel = BackupHistoryViewModel(
            NextCloudService,
            SFTPService,
            GoogleDriveService,
            SeaFileService,
            PackagingService
        )

        Connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        runBlocking {
            CloudServiceUtil.UploadTestPackage(
                Connection,
                PackagingService,
                Context.cacheDir.absolutePath,
                NextCloudService,
                SFTPService,
                GoogleDriveService,
                SeaFileService
            )
        }

        composeRule.setContent {
            val scaffoldState = rememberScaffoldState()

            LaunchedEffect(key1 = true) {
                ViewModel.RestoreFinishedFlow.collect {
                    scaffoldState.snackbarHostState.showSnackbar(
                        it.Message.asString(Context)
                    )
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        modifier = Modifier
                            .testTag("RestoreSnackbar"),
                        hostState = scaffoldState.snackbarHostState
                    ){
                        Snackbar(snackbarData = it)
                    }
                }
            ) {
                BackupHistoryView(
                    paddingValues = it,
                    viewModel = ViewModel
                )
            }
        }
    }

    @After
    fun cleanUp() {
        runBlocking {
            CloudServiceUtil.CleanupServer(
                Connection,
                NextCloudService,
                SFTPService,
                GoogleDriveService,
                SeaFileService
            )
        }
    }

    @Test
    fun BackupHistoryTest() {
        composeRule.onNodeWithTag("ErrorLabel")
            .assertIsDisplayed()

        composeRule.onNodeWithText(Context.getString(R.string.NoFiles))
            .assertIsDisplayed()

        runBlocking {
            ViewModel.InitValues(Connection)
        }

        composeRule.onNodeWithTag("HistoryList")
            .onChildren()
            .assertCountEquals(4)
    }

    @Test
    fun RestoreBackupTest(){
        runBlocking {
            ViewModel.InitValues(Connection)
        }

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("RestoreMenuItem")
            .performClick()

        composeRule.onNodeWithTag("RestoreDialog")
            .assertExists()

        composeRule.onNodeWithTag("RestoreDialogCancel")
            .performClick()

        composeRule.onNodeWithTag("RestoreDialog")
            .assertDoesNotExist()

        val files = TestFileUtil.GetFilesRecursively(Connection)

        for (path in Connection.Paths) {
            val pathFile = File(path.Path)
            pathFile.delete()
        }

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("RestoreMenuItem")
            .performClick()

        composeRule.onNodeWithTag("RestoreDialogYes")
            .performClick()

        composeRule.onNodeWithTag("RestoreDialog")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("CurrentlyRestoringDialog")
            .assertExists()

        Thread.sleep(10000)

        composeRule.onNodeWithTag("RestoreSnackbar")
            .assertExists()

        for (checkFile in files) {
            assert(checkFile.exists())
        }
    }

    @Test
    fun DeleteBackupTest(){
        runBlocking {
            ViewModel.InitValues(Connection)
        }

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithTag("DeleteDialog")
            .assertExists()

        composeRule.onNodeWithTag("DeleteDialogCancel")
            .performClick()

        composeRule.onNodeWithTag("DeleteDialog")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithTag("DeleteDialogYes")
            .performClick()

        Thread.sleep(10000)

        composeRule.onNodeWithTag("DeleteDialog")
            .assertDoesNotExist()

        composeRule.onNodeWithText(Context.getString(R.string.NoFiles))
            .assertExists()
    }
}