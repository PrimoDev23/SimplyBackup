package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.search.AccountSearchService
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.main.AccountOverviewViewModel
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
class AccountsViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var AccountRepository: AccountRepository

    @Inject
    lateinit var AccountSearchService: AccountSearchService

    @Before
    fun setUp() {
        hiltRule.inject()

        val viewModel = AccountOverviewViewModel(
            ConnectionRepository,
            AccountRepository,
            AccountSearchService
        )

        composeRule.setContent {
            SimplyBackupTheme {
                AccountOverview(
                    paddingValues = PaddingValues(0.dp),
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun DeleteAccountTest() {
        InsertAccount()

        composeRule.onNodeWithText("Test1234")
            .assertExists()

        composeRule.onNodeWithTag("More")
            .performClick()

        composeRule.onNodeWithTag("DeleteMenuItem")
            .performClick()

        composeRule.onNodeWithText("Test1234")
            .assertDoesNotExist()
    }

    @Test
    fun AccountSearchTest() {
        InsertAccount()

        composeRule.onNodeWithText("Test1234")
            .assertExists()

        composeRule.onNodeWithTag("SearchField")
            .performTextInput("Test")

        composeRule.onNodeWithText("Test1234")
            .assertExists()

        composeRule.onNodeWithTag("SearchField")
            .performTextReplacement("A")

        composeRule.onNodeWithText("Test1234")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("SearchField")
            .performTextClearance()

        composeRule.onNodeWithText("Test1234")
            .assertExists()

        composeRule.onNodeWithTag("SearchField")
            .performTextReplacement("A")

        composeRule.onNodeWithTag("ClearSearch")
            .performClick()

        composeRule.onNodeWithText("Test1234")
            .assertExists()
    }

    private fun InsertAccount(){
        runBlocking {
            AccountRepository.Insert(
                Account(
                    ConnectionType.GoogleDrive,
                    "Test1234"
                )
            )
        }

        Thread.sleep(1000)
    }
}