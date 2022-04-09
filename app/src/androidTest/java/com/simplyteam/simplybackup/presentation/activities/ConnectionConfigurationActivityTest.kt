package com.simplyteam.simplybackup.presentation.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.simplyteam.simplybackup.common.AppModule
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ConnectionConfigurationActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<ConnectionConfigurationActivity>()

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Before
    fun Setup() {
        hiltRule.inject()
    }

    @Test
    fun AddDeletePath() {
        composeRule.onNodeWithTag("ConfigurePaths")
            .performClick()

        composeRule.onNodeWithTag("CurrentPath")
            .performTextInput("/sdcard/Pictures")

        composeRule.onNodeWithTag("AddPath")
            .performClick()

        composeRule.onNodeWithText("/sdcard/Pictures")
            .assertExists()

        composeRule.onNodeWithTag("DeletePath")
            .performClick()

        composeRule.onNodeWithText("/sdcard/Pictures")
            .assertDoesNotExist()
    }
}