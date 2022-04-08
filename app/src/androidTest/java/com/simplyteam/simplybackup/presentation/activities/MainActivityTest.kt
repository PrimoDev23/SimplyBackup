package com.simplyteam.simplybackup.presentation.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


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
    lateinit var SimplyBackupDatabase: SimplyBackupDatabase

    @Before
    fun Setup() {
        hiltRule.inject()
    }

    @After
    fun Dispose() {
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

        composeRule.onNodeWithTag(Screen.Home.Route)
            .performClick()

        composeRule.onNodeWithTag("History")
            .onChildren()
            .assertCountEquals(1)

        composeRule.onNodeWithText(testValue)
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

    private fun InsertConnection(): Long {
        var id: Long
        runBlocking {
            id = ConnectionRepository.InsertConnection(
                Connection(
                    ConnectionType = ConnectionType.NextCloud,
                    Name = "Test",
                    URL = "Test",
                    Username = "Test",
                    Password = "Test"
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
}