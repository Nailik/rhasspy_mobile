package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.onSwitch
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.viewmodel.settings.LogSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = LogSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                LogSettingsContent(viewModel)
            }
        }

    }

    /**
     * debug is saved
     * debug is selected
     *
     * user clicks error
     * error is selected
     * error is saved
     *
     * show log is false
     * show log false is shown
     * user clicks show log
     * show log true is shown
     * show log true is saved
     *
     * audio frame logging is false
     * audio frame logging false is shown
     * user clicks audio frame logging
     * audio frame logging true is shown
     * audio frame logging true is saved
     */
    @Test
    fun contentTest() {
        //debug is saved
        assertEquals(LogLevel.Debug, viewModel.logLevel.value)
        //debug is selected
        composeTestRule.onNodeWithTag(LogLevel.Debug, true).onChildAt(0).assertIsSelected()

        //user clicks error
        composeTestRule.onNodeWithTag(LogLevel.Error).performClick()
        //error is selected
        composeTestRule.onNodeWithTag(LogLevel.Error, true).onChildAt(0).assertIsSelected()
        //error is saved
        assertEquals(LogLevel.Error, LogSettingsViewModel().logLevel.value)

        //show log is false
        viewModel.toggleShowLogEnabled(false)
        //show log false is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).onSwitch().assertIsOff()
        //user clicks show log
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).performClick()
        //show log true is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).onSwitch().assertIsOn()
        //show log true is saved
        assertTrue { LogSettingsViewModel().isShowLogEnabled.value }

        //audio frame logging is false
        viewModel.toggleLogAudioFramesEnabled(false)
        //audio frame logging false is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).onSwitch().assertIsOff()
        //user clicks audio frame logging
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).performClick()
        //audio frame logging true is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).onSwitch().assertIsOn()
        //audio frame logging true is saved
        assertTrue { LogSettingsViewModel().isLogAudioFramesEnabled.value }
    }


}