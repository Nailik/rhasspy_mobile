package org.rhasspy.mobile.android.settings.content

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Change.SetBackgroundServiceEnabled
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BackgroundSettingsContentTest : FlakyTest() {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<BackgroundServiceSettingsViewModel>()
    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val dialog = " com.android.settings"
    private val acceptButton = "android:id/button1"

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                BackgroundServiceSettingsContent()
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
    fun testContent() = runTest {
        viewModel.onEvent(SetBackgroundServiceEnabled(false))

        //background services disabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOff()

        //user clicks background services
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).performClick()
        composeTestRule.awaitIdle()
        //background services active
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOn()
        //background services enabled saved
        val newViewModel = BackgroundServiceSettingsViewModel(get())
        assertTrue { newViewModel.viewState.value.isBackgroundServiceEnabled }

        if (!viewModel.viewState.value.isBatteryOptimizationDisabled) {
            //deactivate battery optimization visible
            composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).assertIsDisplayed()
            //battery optimization is deactivated
            assertFalse { viewModel.viewState.value.isBatteryOptimizationDisabled }
            //user clicks deactivate battery optimization
            composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).performClick()
            composeTestRule.awaitIdle()
            device.waitForIdle()
            //system dialog is shown
            device.findObject(UiSelector().resourceIdMatches(dialog)).exists()
            //user clicks accept
            device.wait(Until.hasObject(By.res(acceptButton.toPattern())), 5000)
            device.findObject(UiSelector().resourceIdMatches(acceptButton)).click()
        }

    }

}