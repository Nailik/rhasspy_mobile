package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.viewmodel.settings.DeviceSettingsSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = DeviceSettingsSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                DeviceSettingsContent(viewModel)
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
    fun testContent() {
        //Volume is visible
        composeTestRule.onNodeWithTag(TestTag.Volume).assertIsDisplayed()

        //hot word is enabled
        composeTestRule.onNodeWithTag(TestTag.HotWord).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsSettingsViewModel().isHotWordEnabled.value }
        //user clicks hot word
        composeTestRule.onNodeWithTag(TestTag.HotWord).performClick()
        //hot word is disabled
        composeTestRule.onNodeWithTag(TestTag.HotWord).onListItemSwitch().assertIsOff()
        //hot word disabled is saved
        assertFalse { DeviceSettingsSettingsViewModel().isHotWordEnabled.value }

        //audio output is enabled
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsSettingsViewModel().isAudioOutputEnabled.value }
        //user clicks audio output
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).performClick()
        //audio output is disabled
        composeTestRule.onNodeWithTag(TestTag.AudioOutput).onListItemSwitch().assertIsOff()
        //audio output disabled is saved
        assertFalse { DeviceSettingsSettingsViewModel().isAudioOutputEnabled.value }

        //intent handling is enabled
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsSettingsViewModel().isIntentHandlingEnabled.value }
        //user clicks intent handling
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).performClick()
        //intent handling is disabled
        composeTestRule.onNodeWithTag(TestTag.IntentHandling).onListItemSwitch().assertIsOff()
        //intent handling disabled is saved
        assertFalse { DeviceSettingsSettingsViewModel().isIntentHandlingEnabled.value }
    }

}