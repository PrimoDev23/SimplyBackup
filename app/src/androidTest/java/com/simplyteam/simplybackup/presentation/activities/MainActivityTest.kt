package com.simplyteam.simplybackup.presentation.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.simplyteam.simplybackup.BuildConfig
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val _testConnectionType = ConnectionType.NextCloud

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var HistoryRepository: HistoryRepository

    @Inject
    lateinit var AccountRepository: AccountRepository

    @Inject
    lateinit var PackagingService: PackagingService

    @Inject
    lateinit var NextCloudService: NextCloudService

    @Inject
    lateinit var SFTPService: SFTPService

    @Inject
    lateinit var GoogleDriveService: GoogleDriveService

    @Before
    fun Setup() {
        hiltRule.inject()

        if(_testConnectionType == ConnectionType.GoogleDrive){
            runBlocking {
                AccountRepository.Insert(
                    Account(
                        ConnectionType.GoogleDrive,
                        BuildConfig.GOOGLE_DRIVE_USER
                    )
                )

                AccountRepository.Insert(
                    Account(
                        ConnectionType.GoogleDrive,
                        BuildConfig.GOOGLE_DRIVE_EDITUSER
                    )
                )
            }
        }
    }

    @Test
    fun CreateConnectionTest() {
        val connections = RetrieveConnections()
        val testValue = "TestEntry"

        composeRule.onNodeWithTag(Screen.Connections.Route)
            .performClick()

        composeRule.onNodeWithTag("AddConnection")
            .performClick()

        if (_testConnectionType != ConnectionType.NextCloud) {
            composeRule.onNodeWithTag(_testConnectionType.name)
                .performClick()
        }

        composeRule.onNodeWithTag("Name")
            .performTextInput(testValue)

        when(_testConnectionType) {
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
                    .performTextReplacement(testValue)
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

        composeRule.onNodeWithTag("BackButton")
            .performClick()

        composeRule.onNodeWithTag("WifiOnly")
            .performClick()

        composeRule.onNodeWithTag("ScheduleTypeCard")
            .performClick()

        composeRule.onNodeWithTag("MonthlyMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performScrollTo()
            .performClick()

        Thread.sleep(1000)

        val newConnections = RetrieveConnections()
        assertEquals(
            connections.size + 1,
            newConnections.size
        )

        val newConnection = newConnections.last()

        assertEquals(
            _testConnectionType,
            newConnection.ConnectionType
        )
        assertEquals(
            testValue,
            newConnection.Name
        )

        when(_testConnectionType) {
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
                    testValue,
                    newConnection.RemotePath
                )
            }
        }

        assert(newConnection.WifiOnly)
        assertEquals(
            ScheduleType.MONTHLY,
            newConnection.ScheduleType
        )
        assert(newConnection.Paths.isNotEmpty())
    }

    @Test
    fun HistoryEntryOnNewConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        composeRule.onNodeWithTag(Screen.History.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(2)

        composeRule.onNodeWithContentDescription(connection.ConnectionType.name)
            .assertExists()

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun DeleteConnectionTest() {
        ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        composeRule.onNodeWithTag(Screen.Connections.Route)
            .performClick()

        composeRule.onAllNodesWithTag("More")[0]
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertDoesNotExist()

        assertEquals(
            0,
            RetrieveConnections().size
        )

        composeRule.onNodeWithTag("MainSnackbar")
            .assertExists()
    }

    @Test
    fun UndoDeleteConnectionTest() {
        ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        composeRule.onNodeWithTag(Screen.Connections.Route)
            .performClick()

        composeRule.onAllNodesWithTag("More")[0]
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertDoesNotExist()

        assertEquals(
            0,
            RetrieveConnections().size
        )

        composeRule.onNodeWithTag("MainSnackbar")
            .assertExists()

        composeRule.onNodeWithText("Undo")
            .performClick()

        assertEquals(
            1,
            RetrieveConnections().size
        )

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertExists()
    }

    @Test
    fun DeleteRemovesHistoryTest() {
        ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        val connection = RetrieveConnections().last()

        composeRule.onNodeWithTag(Screen.History.Route)
            .performClick()

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertExists()

        RemoveConnection(connection)

        val connections = RetrieveConnections()

        assertEquals(
            0,
            connections.size
        )

        Thread.sleep(1000)

        composeRule.onNodeWithText("AndroidInstrumentationTest")
            .assertDoesNotExist()
    }

    @Test
    fun EditConnectionTest() {
        var connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        val testValue = "ReplaceValue"

        composeRule.onNodeWithTag(Screen.Connections.Route)
            .performClick()

        composeRule.onNodeWithTag(connection.Id.toString())
            .performClick()

        composeRule.onNodeWithTag("${connection.ConnectionType.name}Selected")
            .assertExists()

        composeRule.onNodeWithTag("Name")
            .performTextReplacement(testValue)

        when(_testConnectionType) {
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
                    .performTextReplacement(testValue)
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

        composeRule.onNodeWithTag("BackButton")
            .performClick()

        composeRule.onNodeWithTag("WifiOnly")
            .performClick()

        composeRule.onNodeWithTag("ScheduleTypeCard")
            .performClick()

        composeRule.onNodeWithTag("YearlyMenuItem")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performScrollTo()
            .performClick()

        connection = RetrieveConnections().first {
            it.Id == connection.Id
        }

        assertEquals(
            testValue,
            connection.Name
        )

        when(_testConnectionType){
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
                    testValue,
                    connection.RemotePath
                )
            }
        }

        assert(connection.WifiOnly)
        assertEquals(
            ScheduleType.YEARLY,
            connection.ScheduleType
        )
        assert(connection.Paths.isNotEmpty())

        Thread.sleep(1000)

        composeRule.onNodeWithTag(Screen.History.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(2)

        composeRule.onNodeWithText(testValue)
            .assertExists()
    }

    @Test
    fun RunBackupForConnectionTest() {
        ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        val connection = RetrieveConnections().last()

        composeRule.onNodeWithTag(Screen.Connections.Route)
            .performClick()

        composeRule.onAllNodesWithTag("More")[0]
            .performClick()

        composeRule.onNodeWithTag("BackupMenuItem")
            .performClick()

        composeRule.onNodeWithTag("MainSnackbar")
            .assertExists()

        Thread.sleep(5000)

        CleanupServer(connection)
    }

    @Test
    fun AddHistoryEntryTest() {
        val connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        val historyEntry = HistoryEntry(
            Id = 0,
            ConnectionId = connection.Id,
            Time = LocalDateTime.now()
                .format(Constants.HumanReadableFormatter),
            Succeed = true,
            Size = 2048
        )

        runBlocking {
            HistoryRepository.InsertHistoryEntry(
                historyEntry
            )
        }

        composeRule.onNodeWithText(historyEntry.Time)
            .assertExists()

        composeRule.onNodeWithText("2.0 KB")
            .assertExists()

        val nextDay = LocalDate.now()
            .plusDays(1)
            .atStartOfDay()
            .format(Constants.HumanReadableFormatter)

        composeRule.onNodeWithText(nextDay)
            .assertExists()
    }

    @Test
    fun BackupHistoryTest() {
        val connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        composeRule.onNodeWithTag(connection.Name)
            .performClick()

        composeRule.onNodeWithTag("ProgressIndicator")
            .assertExists()

        Thread.sleep(10000)

        composeRule.onNodeWithTag("ErrorLabel")
            .assertIsDisplayed()

        composeRule.onNodeWithText(composeRule.activity.getString(R.string.NoFiles))
            .assertIsDisplayed()

        composeRule.onNodeWithTag("BackButton")
            .performClick()

        UploadTestPackage(connection)
            .onSuccess {
                if (it) {
                    composeRule.onNodeWithTag(connection.Name)
                        .performClick()

                    Thread.sleep(10000)

                    composeRule.onNodeWithTag("HistoryList")
                        .onChildren()
                        .assertCountEquals(4)

                    CleanupServer(connection)
                }
            }
            .onFailure {
                throw it
            }
    }

    @Test
    fun DeleteBackupTest() {
        val connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        UploadTestPackage(connection)
            .onSuccess {
                if (it) {
                    composeRule.onNodeWithTag(connection.Name)
                        .performClick()

                    Thread.sleep(10000)

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

                    composeRule.onNodeWithText(composeRule.activity.getString(R.string.NoFiles))
                        .assertExists()
                }
            }
            .onFailure {
                throw it
            }
    }

    @Test
    fun RestoreBackupTest() {
        val connection = ConnectionUtil.InsertConnection(_testConnectionType, ConnectionRepository)

        UploadTestPackage(connection)
            .onSuccess {
                if (it) {
                    composeRule.onNodeWithTag(connection.Name)
                        .performClick()

                    Thread.sleep(10000)

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

                    val files = GetFilesRecursively(connection)

                    for (path in connection.Paths) {
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

                    CleanupServer(connection)

                    for (checkFile in files) {
                        assert(checkFile.exists())
                    }
                }
            }
            .onFailure {
                throw it
            }
    }

    private fun RetrieveConnections(): List<Connection> {
        var connections: List<Connection>

        runBlocking {
            connections = ConnectionRepository.GetAllConnections()
        }

        return connections
    }

    private fun RemoveConnection(connection: Connection) {
        runBlocking {
            ConnectionRepository.RemoveConnection(connection)
        }
    }

    private fun GetFilesRecursively(connection: Connection): MutableList<File> {
        val files = mutableListOf<File>()

        for (path in connection.Paths) {
            val file = File(path.Path)

            if (file.isDirectory) {
                val innerFiles = GetFilesForDirectory(file)
                files.addAll(innerFiles)
            } else {
                files.add(file)
            }
        }

        return files
    }

    private fun GetFilesForDirectory(dir: File): MutableList<File> {
        val list = mutableListOf<File>()

        dir.listFiles()
            ?.let { files ->
                for (file in files) {
                    if (file.isDirectory) {
                        list.addAll(GetFilesForDirectory(file))
                    } else {
                        list.add(file)
                    }
                }
            }

        return list
    }

    private fun UploadTestPackage(connection: Connection): Result<Boolean> {
        PackagingService.CreatePackage(
            composeRule.activity.filesDir.absolutePath,
            connection
        )
            .onSuccess {
                val result = runBlocking {
                    when (_testConnectionType) {
                        ConnectionType.NextCloud -> {
                            NextCloudService.UploadFile(
                                connection,
                                it
                            )
                        }
                        ConnectionType.SFTP -> {
                            SFTPService.UploadFile(
                                connection,
                                it
                            )
                        }
                        ConnectionType.GoogleDrive -> {
                            GoogleDriveService.UploadFile(
                                connection,
                                it
                            )
                        }
                    }
                }
                return result
            }
            .onFailure {
                throw it
            }
        return Result.failure(Exception())
    }

    private fun CleanupServer(connection: Connection) {
        val files: List<RemoteFile>
        runBlocking {
            files = when (_testConnectionType) {
                ConnectionType.NextCloud -> {
                    NextCloudService.GetFilesForConnection(
                        connection
                    )
                }
                ConnectionType.SFTP -> {
                    SFTPService.GetFilesForConnection(
                        connection
                    )
                }
                ConnectionType.GoogleDrive -> {
                    GoogleDriveService.GetFilesForConnection(
                        connection
                    )
                }
            }
        }

        for (file in files) {
            runBlocking {
                when (_testConnectionType) {
                    ConnectionType.NextCloud -> {
                        NextCloudService.DeleteFile(
                            connection,
                            file.RemotePath
                        )
                    }
                    ConnectionType.SFTP -> {
                        SFTPService.DeleteFile(
                            connection,
                            file.RemotePath
                        )
                    }
                    ConnectionType.GoogleDrive -> {
                        GoogleDriveService.DeleteFile(
                            connection,
                            file.RemoteId
                        )
                    }
                }
            }
        }
    }
}