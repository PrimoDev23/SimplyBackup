package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.presentation.viewmodels.connection.PathsConfigurationViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class PathsConfigurationViewTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()

        val viewModel = PathsConfigurationViewModel()

        composeRule.setContent {
            PathsConfigurationView(
                paddingValues = PaddingValues(0.dp),
                viewModel = viewModel
            )
        }
    }

    @Test
    fun AddPathTest(){
        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Pictures")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithTag("CurrentPath")
            .assert(hasText(""))

        composeRule.onNodeWithText("/sdcard/Pictures")
            .assertExists()
    }

    @Test
    fun CurrentPathErrorTest(){
        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithTag("CurrentPathError")
            .assertExists()

        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Pictures")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithTag("CurrentPathError")
            .assertDoesNotExist()
    }

    @Test
    fun PathDeleteTest(){
        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Pictures")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithTag("DeletePath")
            .performClick()

        composeRule.onNodeWithText("/sdcard/Pictures")
            .assertDoesNotExist()
    }
}