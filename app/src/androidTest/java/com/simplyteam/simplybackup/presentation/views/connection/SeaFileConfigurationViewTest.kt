package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SeaFileConfigurationViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class SeaFileConfigurationViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    private lateinit var _viewModel: SeaFileConfigurationViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        _viewModel = SeaFileConfigurationViewModel()

        composeRule.setContent {
            SeaFileInformationFields(
                viewModel = _viewModel
            )
        }
    }

    @Test
    fun SeaFileValidationTest(){
        try{
            _viewModel.GetBaseConnection()
            assert(false)
        }catch (ex: Exception){

        }

        composeRule.onNodeWithTag("NameError")
            .assertExists()

        composeRule.onNodeWithTag("HostError")
            .assertExists()

        composeRule.onNodeWithTag("UsernameError")
            .assertExists()

        composeRule.onNodeWithTag("PasswordError")
            .assertExists()

        composeRule.onNodeWithTag("RepoIdError")
            .assertExists()

        composeRule.onNodeWithTag("RemotePathError")
            .assertExists()

        composeRule.onNodeWithTag("RemotePath")
            .performTextInput("Test")

        try{
            _viewModel.GetBaseConnection()
            assert(false)
        }catch (ex: Exception){

        }

        composeRule.onNodeWithTag("RemotePathError")
            .assertExists()

        composeRule.onNodeWithTag("RemotePath")
            .performTextReplacement("/Test/")

        try{
            _viewModel.GetBaseConnection()
            assert(false)
        }catch (ex: Exception){

        }

        composeRule.onNodeWithTag("RemotePathError")
            .assertExists()

        composeRule.onNodeWithTag("Name")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Host")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Username")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Password")
            .performTextInput("Test")

        composeRule.onNodeWithTag("RepoId")
            .performTextInput("Test")

        composeRule.onNodeWithTag("RemotePath")
            .performTextReplacement("/Test")

        try{
            _viewModel.GetBaseConnection()
        }catch (ex: Exception){
            assert(false)
        }

        composeRule.onNodeWithTag("NameError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("HostError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("UsernameError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("PasswordError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("RepoIdError")
            .assertDoesNotExist()

        composeRule.onNodeWithTag("RemotePathError")
            .assertDoesNotExist()
    }

    @Test
    fun SeaFilePasswordVisualTransformationTest(){
        composeRule.onNodeWithTag("Password")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Password,
                    Unit
                )
            )
    }
}