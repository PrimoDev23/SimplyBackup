package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NextCloudConfigurationViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    private lateinit var _viewModel: NextCloudConfigurationViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        _viewModel = NextCloudConfigurationViewModel()

        composeRule.setContent {
            NextCloudInformationFields(
                viewModel = _viewModel
            )
        }
    }

    @Test
    fun NextcloudValidationTest(){
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

        composeRule.onNodeWithTag("Name")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Host")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Username")
            .performTextInput("Test")

        composeRule.onNodeWithTag("Password")
            .performTextInput("Test")

        try{
            _viewModel.GetBaseConnection()
        }catch (ex: Exception){
            assert(false)
        }
    }

    @Test
    fun NextCloudPasswordVisualTransformationTest(){
        composeRule.onNodeWithTag("Password")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Password,
                    Unit
                )
            )
    }
}