package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.DeviceSettingsContent
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceSettingsContentTest : FlakyTest() {

    private val viewModel = get<DeviceSettingsViewModel>()

    @Composable
    override fun ComposableContent() {
        DeviceSettingsContent(viewModel)
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
    @AllowFlaky
    fun testContent() = runTest {
        setupContent()

        //Volume is visible
        composeTestRule.onNodeWithTag(TestTag.Volume).assertIsDisplayed()

        //mqtt api enabled
        composeTestRule.onNodeWithTag(TestTag.MqttApi).onListItemSwitch().assertIsOff()
        assertFalse { DeviceSettingsViewModel(get()).viewState.value.isMqttApiDeviceChangeEnabled }
        //user clicks hot word
        composeTestRule.onNodeWithTag(TestTag.MqttApi).performClick()
        composeTestRule.awaitIdle()
        //hot word is disabled
        composeTestRule.onNodeWithTag(TestTag.MqttApi).onListItemSwitch().assertIsOn()
        //hot word disabled is saved
        assertTrue { DeviceSettingsViewModel(get()).viewState.value.isMqttApiDeviceChangeEnabled }

        //http api is enabled
        composeTestRule.onNodeWithTag(TestTag.HttpApi).onListItemSwitch().assertIsOn()
        assertTrue { DeviceSettingsViewModel(get()).viewState.value.isHttpApiDeviceChangeEnabled }
        //user clicks hot word
        composeTestRule.onNodeWithTag(TestTag.HttpApi).performClick()
        composeTestRule.awaitIdle()
        //hot word is disabled
        composeTestRule.onNodeWithTag(TestTag.HttpApi).onListItemSwitch().assertIsOff()
        //hot word disabled is saved
        assertFalse { DeviceSettingsViewModel(get()).viewState.value.isHttpApiDeviceChangeEnabled }

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