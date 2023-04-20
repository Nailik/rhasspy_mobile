package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.SetShowLogEnabled
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogSettingsContentTest : KoinComponent {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<LogSettingsViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                LogSettingsContent()
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
        assertEquals(LogLevel.Debug, viewModel.viewState.value.logLevel)
        //debug is selected
        composeTestRule.onNodeWithTag(LogLevel.Debug, true).onListItemRadioButton().assertIsSelected()

        //user clicks error
        composeTestRule.onNodeWithTag(LogLevel.Error).performClick()
        //error is selected
        composeTestRule.onNodeWithTag(LogLevel.Error, true).onListItemRadioButton().assertIsSelected()
        //error is saved
        assertEquals(LogLevel.Error, LogSettingsViewModel(get()).viewState.value.logLevel)

        //show log is false
        viewModel.onEvent(SetShowLogEnabled(false))
        //show log false is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).onListItemSwitch().assertIsOff()
        //user clicks show log
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).performClick()
        //show log true is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).onListItemSwitch().assertIsOn()
        //show log true is saved
        assertTrue { LogSettingsViewModel(get()).viewState.value.isShowLogEnabled }

        //audio frame logging is false
        viewModel.onEvent(SetShowLogEnabled(false))
        //audio frame logging false is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).onListItemSwitch().assertIsOff()
        //user clicks audio frame logging
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).performClick()
        //audio frame logging true is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).onListItemSwitch().assertIsOn()
        //audio frame logging true is saved
        assertTrue { LogSettingsViewModel(get()).viewState.value.isLogAudioFramesEnabled }
    }


}