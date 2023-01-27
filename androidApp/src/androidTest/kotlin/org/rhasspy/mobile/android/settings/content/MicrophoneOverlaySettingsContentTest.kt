package org.rhasspy.mobile.android.settings.content

import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
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
import org.rhasspy.mobile.android.onSwitch
import org.rhasspy.mobile.android.resetOverlayPermission
import org.rhasspy.mobile.android.text
import org.rhasspy.mobile.logic.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.MicrophoneOverlaySettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MicrophoneOverlaySettingsContentTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = MicrophoneOverlaySettingsViewModel()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"


    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                MicrophoneOverlaySettingsContent(viewModel)
            }
        }

    }

    /**
     * OverlaySizeOption is disabled
     * Disabled is selected
     * Visible while app is not visible
     *
     * User clicks medium
     *
     * Overlay permission info is displayed
     * user clicks ok
     * user accepts permission
     *
     * Medium is selected
     * Visible while app is visible
     * Medium is saved
     *
     * Visible while app is disabled
     * element is turned off
     *
     * user clicks visible while app
     * element is turned on
     * visible while app is saved
     */
    @Test
    fun testContent() = runBlocking {
        device.resetOverlayPermission(composeTestRule.activity)

        viewModel.selectMicrophoneOverlayOptionSize(MicrophoneOverlaySizeOption.Disabled)
        viewModel.toggleMicrophoneOverlayWhileAppEnabled(false)

        //OverlaySizeOption is disabled
        assertEquals(
            MicrophoneOverlaySizeOption.Disabled,
            viewModel.microphoneOverlaySizeOption.value
        )
        //Disabled is selected
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Disabled, true).onChildAt(0)
            .assertIsSelected()
        //Visible while app is not visible
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).assertDoesNotExist()

        //User clicks medium
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Medium).performClick()

        //Overlay permission info is displayed
        composeTestRule.awaitIdle()
        //user clicks ok
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //user accepts permission
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
        UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName))
        device.findObject(UiSelector().text(MR.strings.appName)).click()
        device.findObject(UiSelector().className(Switch::class.java)).click()
        //User clicks back
        device.pressBack()
        device.pressBack()
        composeTestRule.awaitIdle()

        //Medium is selected
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Medium, true).onChildAt(0)
            .assertIsSelected()
        //Visible while app is visible
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).assertIsDisplayed()
        //Medium is saved
        var newViewModel = MicrophoneOverlaySettingsViewModel()
        assertEquals(
            MicrophoneOverlaySizeOption.Medium,
            newViewModel.microphoneOverlaySizeOption.value
        )

        //Visible while app is disabled
        assertFalse { viewModel.isMicrophoneOverlayWhileAppEnabled.value }
        //element is turned off
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).onSwitch().assertIsOff()

        //user clicks visible while app
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).performClick()
        //element is turned on
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).onSwitch().assertIsOn()
        //visible while app is saved
        newViewModel = MicrophoneOverlaySettingsViewModel()
        assertTrue { newViewModel.isMicrophoneOverlayWhileAppEnabled.value }

    }
}