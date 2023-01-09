package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.assertTextEquals
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.onSwitch
import org.rhasspy.mobile.viewmodel.settings.BackgroundServiceSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackgroundSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = BackgroundServiceSettingsViewModel()
    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val dialog = " com.android.settings"
    private val acceptButton = "android:id/button1"

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                BackgroundServiceSettingsContent(viewModel)
            }
        }

    }

    /**
     * background services disabled
     * deactivate battery optimization invisible
     *
     * user clicks background services
     * background services active
     * deactivate battery optimization visible
     * background services enabled saved
     *
     * battery optimization is deactivated
     * user clicks deactivate battery optimization
     * system dialog is shown
     * user clicks accept
     *
     * deactivate battery optimization is shown as enabled
     */
    @Test
    fun testContent() {
        viewModel.toggleBackgroundServiceEnabled(false)

        //background services disabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onSwitch().assertIsOff()
        //deactivate battery optimization invisible
        composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).assertDoesNotExist()

        //user clicks background services
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).performClick()
        //background services active
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onSwitch().assertIsOn()
        //deactivate battery optimization visible
        composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).assertIsDisplayed()
        //background services enabled saved
        val newViewModel = BackgroundServiceSettingsViewModel()
        assertTrue { newViewModel.isBackgroundServiceEnabled.value }

        //battery optimization is deactivated
        assertFalse { viewModel.isBatteryOptimizationDisabled.value }
        //user clicks deactivate battery optimization
        composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).performClick()
        //system dialog is shown
        device.findObject(UiSelector().resourceIdMatches(dialog)).exists()
        //user clicks accept
        device.findObject(UiSelector().resourceIdMatches(acceptButton)).click()
        //deactivate battery optimization is shown as enabled
        composeTestRule.onNodeWithTag(TestTag.BatteryOptimization, true).onChildAt(2)
            .assertTextEquals(MR.strings.enabled)

    }

}