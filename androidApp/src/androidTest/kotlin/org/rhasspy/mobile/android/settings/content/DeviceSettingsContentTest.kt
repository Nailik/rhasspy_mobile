package org.rhasspy.mobile.android.settings.content

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.DeviceSettingsContent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceSettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                DeviceSettingsContent()
            }
        }

    }

    /**
     * Volume is visible
     *
     * hot word is enabled
     * user clicks hot word
     * hot word is disabled
     * hot word disabled is saved
     *
     * audio output is enabled
     * user clicks audio output
     * audio output is disabled
     * audio output disabled is saved
     *
     * intent handling is enabled
     * user clicks intent handling
     * intent handling is disabled
     * intent handling disabled is saved
     */
    @Test
    fun testContent() = runTest {
        //Volume is visible
        composeTestRule.onNodeWithTag(TestTag.Volume).assertIsDisplayed()

        //hot word is enabled
        composeTestRule.onNodeWithTag(TestTag.HotWord).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsViewModel(get()).viewState.value.isHotWordEnabled }
        //user clicks hot word
        composeTestRule.onNodeWithTag(TestTag.HotWord).performClick()
        composeTestRule.awaitIdle()
        //hot word is disabled
        composeTestRule.onNodeWithTag(TestTag.HotWord).onListItemSwitch().assertIsOff()
        //hot word disabled is saved
        assertFalse { DeviceSettingsViewModel(get()).viewState.value.isHotWordEnabled }

        //audio output is enabled
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsViewModel(get()).viewState.value.isAudioOutputEnabled }
        //user clicks audio output
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).performClick()
        composeTestRule.awaitIdle()
        //audio output is disabled
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).onListItemSwitch().assertIsOff()
        //audio output disabled is saved
        assertFalse { DeviceSettingsViewModel(get()).viewState.value.isAudioOutputEnabled }

        //intent handling is enabled
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsViewModel(get()).viewState.value.isIntentHandlingEnabled }
        //user clicks intent handling
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).performClick()
        composeTestRule.awaitIdle()
        //intent handling is disabled
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).onListItemSwitch().assertIsOff()
        //intent handling disabled is saved
        assertFalse { DeviceSettingsViewModel(get()).viewState.value.isIntentHandlingEnabled }
    }

}