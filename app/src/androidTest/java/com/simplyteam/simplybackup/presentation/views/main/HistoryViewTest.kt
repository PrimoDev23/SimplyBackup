package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.common.TestConstants
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.search.HistorySearchService
import com.simplyteam.simplybackup.data.utils.ConnectionUtil
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class HistoryViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var HistoryRepository: HistoryRepository

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var HistorySearchService: HistorySearchService

    @Before
    fun setUp() {
        hiltRule.inject()

        val viewModel = HistoryViewModel(
            HistorySearchService
        )

        composeRule.setContent {
            HistoryView(
                paddingValues = PaddingValues(0.dp),
                viewModel = viewModel
            )
        }
    }

    @Test
    fun AddConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        composeRule.onNodeWithText(connection.Name)
            .assertExists()
    }

    @Test
    fun RemoveConnectionTest() {
        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        composeRule.onNodeWithText(connection.Name)
            .assertExists()

        runBlocking {
            ConnectionRepository.RemoveConnection(connection)
        }

        Thread.sleep(1000)

        composeRule.onNodeWithText(connection.Name)
            .assertDoesNotExist()
    }

    @Test
    fun AddHistoryEntryTest() {
        val connection = ConnectionUtil.InsertConnection(
            TestConstants.TestConnectionType,
            ConnectionRepository
        )

        composeRule.onNodeWithText(connection.Name)
            .assertExists()

        val time = LocalDateTime.now()
            .format(Constants.HumanReadableFormatter)
        val size = 2048L

        runBlocking {
            HistoryRepository.InsertHistoryEntry(
                HistoryEntry(
                    ConnectionId = connection.Id,
                    Size = size,
                    Time = time,
                    Succeed = true
                )
            )
        }

        Thread.sleep(1000)

        composeRule.onNodeWithText(time)
            .assertExists()

        composeRule.onNodeWithText(MathUtil.GetBiggestFileSizeString(size))
            .assertExists()
    }
}