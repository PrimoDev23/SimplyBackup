package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.simplyteam.simplybackup.BuildConfig
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.presentation.viewmodels.connection.GoogleDriveConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SFTPConfigurationViewModel
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
class GoogleDriveConfigurationViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var AccountRepository: AccountRepository

    @Inject
    lateinit var GoogleDriveService: GoogleDriveService

    private lateinit var _viewModel: GoogleDriveConfigurationViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        _viewModel = GoogleDriveConfigurationViewModel(
            AccountRepository,
            GoogleDriveService
        )

        composeRule.setContent {
            GoogleDriveConfigurationView(
                viewModel = _viewModel
            )
        }
    }

    @Test
    fun GoogleDriveErrorTest(){
        runBlocking {
            AccountRepository.Insert(
                Account(
                    ConnectionType.GoogleDrive,
                    "Test"
                )
            )
        }

        try {
            _viewModel.GetBaseConnection()
            assert(false)
        }catch (ex: Exception){

        }

        composeRule.onNodeWithTag("NameError")
            .assertExists()

        composeRule.onNodeWithTag("LoginCardError")
            .performClick()

        composeRule.onNodeWithTag("TestRadio")
            .performClick()

        composeRule.onNodeWithTag("OK")
            .performClick()

        composeRule.onNodeWithTag("Name")
            .performTextInput("Test")

        try {
            _viewModel.GetBaseConnection()
        }catch (ex: Exception){
            assert(false)
        }

        composeRule.onNodeWithTag("NameError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("LoginCardError")
            .assertDoesNotExist()
    }

    @Test
    fun AccountsChangedTest(){
        composeRule.onNodeWithTag("LoginCard")
            .performClick()

        composeRule.onNodeWithTag("TestRadio")
            .assertDoesNotExist()

        runBlocking {
            AccountRepository.Insert(
                Account(
                    ConnectionType.GoogleDrive,
                    "Test"
                )
            )
        }

        composeRule.onNodeWithTag("LoginCard")
            .performClick()

        composeRule.onNodeWithTag("TestRadio")
            .assertExists()
    }
}