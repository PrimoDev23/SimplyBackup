package com.simplyteam.simplybackup.presentation.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.owncloud.android.lib.common.operations.RemoteOperationResult
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.simplyteam.simplybackup.BuildConfig
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.*
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.utils.MathUtil
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.io.path.Path


@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var HistoryRepository: HistoryRepository

    @Inject
    lateinit var SimplyBackupDatabase: SimplyBackupDatabase

    @Inject
    lateinit var PackagingService: PackagingService

    @Inject
    lateinit var NextCloudService: NextCloudService

    @Before
    fun Setup() {
        hiltRule.inject()
    }

    @Test
    fun CreateNextCloudConnection() {
        val connections = RetrieveConnections()
        val testValue = "TestEntry"

        composeRule.onNodeWithTag(Screen.CloudOverview.Route)
            .performClick()

        composeRule.onNodeWithTag("AddConnection")
            .performClick()

        composeRule.onNodeWithTag("NextCloud")
            .performClick()

        composeRule.onNodeWithTag("Name")
            .performTextInput(testValue)

        composeRule.onNodeWithTag("URL")
            .performTextInput(testValue)

        composeRule.onNodeWithTag("Username")
            .performTextInput(testValue)

        composeRule.onNodeWithTag("Password")
            .performTextInput(testValue)

        composeRule.onNodeWithTag("RemotePath")
            .performTextInput(testValue)

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

        composeRule.onNodeWithTag("MonthlyScheduleType")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performClick()

        val newConnections = RetrieveConnections()
        assert(newConnections.size == connections.size + 1)

        val newConnection = newConnections.last()

        assert(newConnection.ConnectionType == ConnectionType.NextCloud)
        assert(newConnection.Name == testValue)
        assert(newConnection.URL == testValue)
        assert(newConnection.Username == testValue)
        assert(newConnection.Password == testValue)
        assert(newConnection.RemotePath == testValue)
        assert(newConnection.WifiOnly)
        assert(newConnection.ScheduleType == ScheduleType.MONTHLY)
        assert(newConnection.Paths.isNotEmpty())
    }

    @Test
    fun HistoryEntryOnNewConnection() {
        val id = InsertConnection()

        val connection = RetrieveConnections().first {
            it.Id == id
        }

        composeRule.onNodeWithTag(Screen.Home.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(1)

        composeRule.onNodeWithContentDescription(connection.ConnectionType.name)
            .assertExists()

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun DeleteConnection() {
        InsertConnection()

        val connectionSize = RetrieveConnections().size

        composeRule.onNodeWithTag(Screen.CloudOverview.Route)
            .performClick()

        val deleteButton =
            composeRule.onAllNodesWithContentDescription(composeRule.activity.getString(R.string.Delete))[0]

        deleteButton.performClick()

        assert(RetrieveConnections().size == connectionSize - 1)

        composeRule.onNodeWithTag(Screen.Home.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(0)

        composeRule.onNodeWithText("Test")
            .assertDoesNotExist()
    }

    @Test
    fun EditConnection() {
        val id = InsertConnection()
        val testValue = "ReplaceValue"

        composeRule.onNodeWithTag(Screen.CloudOverview.Route)
            .performClick()

        composeRule.onNodeWithTag(id.toString())
            .performClick()

        composeRule.onNodeWithTag("Name")
            .performTextReplacement(testValue)

        composeRule.onNodeWithTag("URL")
            .performTextReplacement(testValue)

        composeRule.onNodeWithTag("Username")
            .performTextReplacement(testValue)

        composeRule.onNodeWithTag("Password")
            .performTextReplacement(testValue)

        composeRule.onNodeWithTag("RemotePath")
            .performTextInput(testValue)

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

        composeRule.onNodeWithTag("YearlyScheduleType")
            .performClick()

        composeRule.onNodeWithTag("Save")
            .performClick()

        val connection = RetrieveConnections().first {
            it.Id == id
        }

        assert(connection.ConnectionType == ConnectionType.NextCloud)
        assert(connection.Name == testValue)
        assert(connection.URL == testValue)
        assert(connection.Username == testValue)
        assert(connection.Password == testValue)
        assert(connection.RemotePath == testValue)
        assert(connection.WifiOnly)
        assert(connection.ScheduleType == ScheduleType.YEARLY)
        assert(connection.Paths.isNotEmpty())

        composeRule.onNodeWithTag(Screen.Home.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(1)

        composeRule.onNodeWithText(testValue)
            .assertExists()
    }

    @Test
    fun AddHistoryEntry() {
        val id = InsertConnection()

        val historyEntry = HistoryEntry(
            Id = 0,
            ConnectionId = id,
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
    fun BackupHistory() {
        var id: Long
        runBlocking {
            id = ConnectionRepository.InsertConnection(
                Connection(
                    ConnectionType = ConnectionType.NextCloud,
                    Name = "Test",
                    URL = BuildConfig.NEXTCLOUD_URL,
                    Username = BuildConfig.NEXTCLOUD_USERNAME,
                    Password = BuildConfig.NEXTCLOUD_PASSWORD,
                    Paths = listOf(
                        Path(
                            "/sdcard/Pictures",
                            PathType.DIRECTORY
                        )
                    )
                )
            )
        }

        val connection = RetrieveConnections().first {
            it.Id == id
        }

        composeRule.onNodeWithTag(connection.Name)
            .performClick()

        composeRule.onNodeWithTag("ProgressIndicator")
            .assertIsDisplayed()

        Thread.sleep(10000)

        composeRule.onNodeWithTag("ErrorLabel")
            .assertIsDisplayed()

        composeRule.onNodeWithText(composeRule.activity.getString(R.string.NoFiles))
            .assertIsDisplayed()

        composeRule.onNodeWithTag("BackButton")
            .performClick()

        PackagingService.CreatePackage(
            composeRule.activity,
            connection
        )
            .onSuccess {
                val result: Result<RemoteOperationResult<*>>
                runBlocking {
                    result = NextCloudService.UploadFile(
                        composeRule.activity,
                        connection,
                        it
                    )
                }

                result
                    .onSuccess {
                        if (it.isSuccess) {
                            composeRule.onNodeWithTag(connection.Name)
                                .performClick()

                            Thread.sleep(10000)

                            composeRule.onNodeWithTag("HistoryList")
                                .onChildren()
                                .assertCountEquals(1)

                            DeleteAllNextCloudFiles(connection)
                        } else {
                            throw it.exception
                        }
                    }
                    .onFailure {
                        throw it
                    }
            }
            .onFailure {
                throw it
            }
    }

    @Test
    fun DeleteBackup() {
        val id = InsertConnection()

        val connection = RetrieveConnections().first {
            it.Id == id
        }

        PackagingService.CreatePackage(
            composeRule.activity,
            connection
        )
            .onSuccess {
                val result: Result<RemoteOperationResult<*>>
                runBlocking {
                    result = NextCloudService.UploadFile(
                        composeRule.activity,
                        connection,
                        it
                    )
                }

                result
                    .onSuccess {
                        if (it.isSuccess) {
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
                        } else {
                            throw it.exception
                        }
                    }
                    .onFailure {
                        throw it
                    }
            }
            .onFailure {
                throw it
            }
    }

    @Test
    fun RestoreBackup() {
        val id = InsertConnection()

        val connection = RetrieveConnections().first {
            it.Id == id
        }

        PackagingService.CreatePackage(
            composeRule.activity,
            connection
        )
            .onSuccess {
                val result: Result<RemoteOperationResult<*>>
                runBlocking {
                    result = NextCloudService.UploadFile(
                        composeRule.activity,
                        connection,
                        it
                    )
                }

                result
                    .onSuccess {
                        if (it.isSuccess) {
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

                            for (path in connection.Paths){
                                val pathFile = File(path.Path)
                                pathFile.delete()
                            }

                            composeRule.onNodeWithTag("More")
                                .performClick()

                            composeRule.onNodeWithTag("RestoreMenuItem")
                                .performClick()

                            composeRule.onNodeWithTag("RestoreDialogYes")
                                .performClick()

                            Thread.sleep(10000)

                            DeleteAllNextCloudFiles(connection)

                            composeRule.onNodeWithTag("RestoreDialog")
                                .assertDoesNotExist()

                            for (checkFile in files) {
                                assert(checkFile.exists())
                            }
                        } else {
                            throw it.exception
                        }
                    }
                    .onFailure {
                        throw it
                    }
            }
            .onFailure {
                throw it
            }
    }

    private fun InsertConnection(): Long {
        var id: Long
        runBlocking {
            id = ConnectionRepository.InsertConnection(
                Connection(
                    ConnectionType = ConnectionType.NextCloud,
                    Name = "AndroidInstrumentationTest",
                    URL = BuildConfig.NEXTCLOUD_URL,
                    Username = BuildConfig.NEXTCLOUD_USERNAME,
                    Password = BuildConfig.NEXTCLOUD_PASSWORD,
                    Paths = listOf(
                        Path(
                            "/sdcard/TestFolder",
                            PathType.DIRECTORY
                        )
                    )
                )
            )
        }
        return id
    }

    private fun RetrieveConnections(): List<Connection> {
        var connections: List<Connection>

        runBlocking {
            connections = ConnectionRepository.GetAllConnections(composeRule.activity)
        }

        return connections
    }

    private fun GetFilesRecursively(connection: Connection) : MutableList<File> {
        val files = mutableListOf<File>()

        for (path in connection.Paths) {
            val file = File(path.Path)

            if (file.isDirectory) {
                val innerFiles = GetFilesForDirectory(file)
                files.addAll(innerFiles)
            }else{
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

    private fun DeleteAllNextCloudFiles(connection: Connection) {
        val files: List<RemoteFile>
        runBlocking {
            files = NextCloudService.GetFilesForConnection(
                composeRule.activity,
                connection
            )
        }

        for (file in files) {
            runBlocking {
                NextCloudService.DeleteFile(
                    composeRule.activity,
                    connection,
                    file
                )
            }
        }
    }
}