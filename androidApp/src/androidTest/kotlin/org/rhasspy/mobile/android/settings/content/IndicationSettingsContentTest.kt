package org.rhasspy.mobile.android.settings.content

import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.onListItemRadioButton
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.resetOverlayPermission
import org.rhasspy.mobile.android.settings.content.sound.IndicationSettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.text
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption
import org.rhasspy.mobile.viewmodel.settings.IndicationSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IndicationSettingsContentTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = IndicationSettingsViewModel()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"

    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            Surface(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag(TestTag.Background)
            ) {
                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalMainNavController provides navController
                ) {
                    WakeWordIndicationSettingsContent(viewModel)
                }
            }
        }

    }

    /**
     * wake up display disabled
     * visual disabled
     * sound disabled
     *
     * user clicks wake up display
     * wake up display is enabled
     * wake up display is saved
     *
     * user clicks visual
     * visual is enabled
     * visual is saved
     *
     * user clicks sound
     * sound is enabled
     * sound is saved
     */
    @Test
    fun testIndicationSettings() = runBlocking {
        device.resetOverlayPermission(composeTestRule.activity)

        viewModel.toggleWakeWordDetectionTurnOnDisplay(false)
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            viewModel.toggleWakeWordLightIndicationEnabled()
        }
        viewModel.toggleWakeWordSoundIndicationEnabled(false)

        //wake up display disabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).onListItemSwitch()
            .assertIsOff()
        //visual disabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).onListItemSwitch()
            .assertIsOff()
        //sound disabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch().assertIsOff()

        //user clicks wake up display
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).performClick()
        //wake up display is enabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).onListItemSwitch()
            .assertIsOn()
        //wake up display is saved
        assertTrue { IndicationSettingsViewModel().isWakeWordDetectionTurnOnDisplayEnabled.value }


        //user clicks visual
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).performClick()
        //user accepts permission
        //Ok clicked
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //on Q app is restarted when allowing overlay permission

        //Redirected to settings
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
        UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName))
        device.findObject(UiSelector().text(MR.strings.appName)).click()
        device.findObject(UiSelector().className(Switch::class.java)).click()
        //User clicks back
        device.pressBack()
        device.pressBack()

        //app will be closed when pressing one more back
        OverlayPermission.update()
        //Dialog is closed and permission granted
        assertTrue { OverlayPermission.granted.value }

        //visual is enabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).onListItemSwitch()
            .assertIsOn()
        //visual is saved
        assertTrue { IndicationSettingsViewModel().isWakeWordLightIndicationEnabled.value }

        //user clicks sound
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).performClick()
        //sound is enabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch().assertIsOn()
        //sound is saved
        assertTrue { IndicationSettingsViewModel().isSoundIndicationEnabled.value }
    }

    /**
     * Sound is disabled
     * sound settings invisible
     *
     * user clicks sound
     * sound is enabled
     * sound settings visible
     *
     * sound output is notification
     * sound output notification is selected
     *
     * user clicks sound output sound
     * sound output sound is selected
     * sound output sound is saved
     *
     * user clicks wake word
     * wake word page is opened
     * user clicks back
     *
     * user clicks recorded
     * recorded page is opened
     * user clicks back
     *
     * user click error
     * error page is opened
     * user clicks back
     */
    @Test
    fun testSoundIndicationOptions() {
        //Sound is disabled
        viewModel.toggleWakeWordSoundIndicationEnabled(false)
        //sound settings invisible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.WakeIndicationSound)
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.RecordedIndicationSound)
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.ErrorIndicationSound)
            .assertDoesNotExist()

        //user clicks sound
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).performClick()
        //sound is enabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch().assertIsOn()
        //sound settings visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.WakeIndicationSound)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.RecordedIndicationSound)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.ErrorIndicationSound)
            .assertIsDisplayed()

        //sound output is notification
        assertEquals(AudioOutputOption.Notification, viewModel.soundIndicationOutputOption.value)
        //sound output notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onListItemRadioButton().assertIsSelected()

        //user clicks sound output sound
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound).performClick()
        //sound output sound is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onListItemRadioButton().assertIsSelected()
        //sound output sound is saved
        assertEquals(
            AudioOutputOption.Sound,
            IndicationSettingsViewModel().soundIndicationOutputOption.value
        )

        //user clicks wake word
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.WakeIndicationSound).performClick()
        //wake word page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.WakeIndicationSound)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()

        //user clicks recorded
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.RecordedIndicationSound)
            .performClick()
        //recorded page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.RecordedIndicationSound)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()

        //user click error
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.ErrorIndicationSound).performClick()
        //error page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreens.ErrorIndicationSound)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
    }

}