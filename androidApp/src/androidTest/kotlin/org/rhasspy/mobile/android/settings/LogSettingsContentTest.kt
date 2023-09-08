package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.LogSettingsContent
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.SetShowLogEnabled
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogSettingsContentTest : FlakyTest() {

    private val viewModel = get<LogSettingsViewModel>()

    @Composable
    override fun ComposableContent() {
        LogSettingsContent(viewModel)
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
    @AllowFlaky
    fun contentTest() = runTest {
        setupContent()
        AppSetting.logLevel.value = LogLevel.Debug

        //debug is saved
        assertEquals(LogLevel.Debug, viewModel.viewState.value.logLevel)
        //debug is selected
        composeTestRule.onNodeWithTag(LogLevel.Debug, true).performScrollTo().onListItemRadioButton().assertIsSelected()

        //user clicks error
        composeTestRule.onNodeWithTag(LogLevel.Error).performClick()
        composeTestRule.awaitIdle()
        //error is selected
        composeTestRule.onNodeWithTag(LogLevel.Error, true).performScrollTo().onListItemRadioButton().assertIsSelected()
        //error is saved
        assertEquals(LogLevel.Error, LogSettingsViewModel(get(), get()).viewState.value.logLevel)

        //show log is false
        viewModel.onEvent(SetShowLogEnabled(false))
        composeTestRule.awaitIdle()
        //show log false is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).performScrollTo().onListItemSwitch().assertIsOff()
        //user clicks show log
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).performClick()
        composeTestRule.awaitIdle()
        //show log true is shown
        composeTestRule.onNodeWithTag(TestTag.ShowLogEnabled).performScrollTo().onListItemSwitch().assertIsOn()
        //show log true is saved
        assertTrue { LogSettingsViewModel(get(), get()).viewState.value.isShowLogEnabled }

        //audio frame logging is false
        viewModel.onEvent(SetShowLogEnabled(false))
        composeTestRule.awaitIdle()
        //audio frame logging false is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).performScrollTo().onListItemSwitch().assertIsOff()
        //user clicks audio frame logging
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).performClick()
        composeTestRule.awaitIdle()
        //audio frame logging true is shown
        composeTestRule.onNodeWithTag(TestTag.AudioFramesEnabled).performScrollTo().onListItemSwitch().assertIsOn()
        //audio frame logging true is saved
        assertTrue { LogSettingsViewModel(get(), get()).viewState.value.isLogAudioFramesEnabled }
    }


}