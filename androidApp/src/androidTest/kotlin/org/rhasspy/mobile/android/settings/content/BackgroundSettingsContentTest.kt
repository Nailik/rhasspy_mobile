package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.BackgroundServiceSettingsContent
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackgroundSettingsContentTest : FlakyTest() {

    private val viewModel = get<BackgroundServiceSettingsViewModel>()
    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val dialog = " com.android.settings"
    private val acceptButton = "android:id/button1"

    @Composable
    override fun ComposableContent() {
        BackgroundServiceSettingsContent()
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
    @AllowFlaky
    fun testContent() = runTest {
        setupContent()

        viewModel.onEvent(SetBackgroundServiceSettingsEnabled(false))
        composeTestRule.awaitIdle()

        //background services disabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOff()

        //user clicks background services
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).performClick()
        composeTestRule.awaitIdle()
        //background services active
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOn()
        //background services enabled saved
        assertTrue { AppSetting.isBackgroundServiceEnabled.value }

        if (!viewModel.viewState.value.isBatteryOptimizationDeactivationEnabled) {
            //deactivate battery optimization visible
            composeTestRule.onNodeWithTag(TestTag.BatteryOptimization).assertIsDisplayed()
            //battery optimization is deactivated
            assertFalse { viewModel.viewState.value.isBatteryOptimizationDeactivationEnabled }
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