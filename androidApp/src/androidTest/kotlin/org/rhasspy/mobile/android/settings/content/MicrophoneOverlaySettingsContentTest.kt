package org.rhasspy.mobile.android.settings.content

import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.MicrophoneOverlaySettingsContent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.app.MainActivity
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlayWhileAppEnabled
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MicrophoneOverlaySettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = get<MicrophoneOverlaySettingsViewModel>()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"


    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            TestContentProvider {
                MicrophoneOverlaySettingsContent()
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
    fun testContent() {
        device.resetOverlayPermission(composeTestRule.activity, get())

        viewModel.onEvent(SelectMicrophoneOverlaySizeOption(MicrophoneOverlaySizeOption.Disabled))
        viewModel.onEvent(SetMicrophoneOverlayWhileAppEnabled(false))

        //OverlaySizeOption is disabled
        assertEquals(
            MicrophoneOverlaySizeOption.Disabled,
            viewModel.viewState.value.microphoneOverlaySizeOption
        )
        //Disabled is selected
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Disabled, true)
            .onListItemRadioButton().assertIsSelected()
        //Visible while app is not visible
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).assertDoesNotExist()

        //User clicks medium
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Medium).performClick()

        //Overlay permission info is displayed
        //user clicks ok
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //user accepts permission
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(settingsPage.toPattern())), 5000)
        assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
        UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName.stable))
        device.waitForIdle()
        device.findObject(UiSelector().text(MR.strings.appName.stable)).click()
        device.findObject(UiSelector().className(Switch::class.java)).click()
        //User clicks back
        device.pressBack()
        device.pressBack()

        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Medium, true)
            .onListItemRadioButton().performClick()
        //Medium is selected
        composeTestRule.onNodeWithTag(MicrophoneOverlaySizeOption.Medium, true)
            .onListItemRadioButton().assertIsSelected()
        //Visible while app is visible
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).assertIsDisplayed()
        //Medium is saved
        var newViewModel = MicrophoneOverlaySettingsViewModel(get())
        assertEquals(
            MicrophoneOverlaySizeOption.Medium,
            newViewModel.viewState.value.microphoneOverlaySizeOption
        )

        //Visible while app is disabled
        assertFalse { newViewModel.viewState.value.isMicrophoneOverlayWhileAppEnabled }
        //element is turned off
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).onListItemSwitch()
            .assertIsOff()

        //user clicks visible while app
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).performClick()
        //element is turned on
        composeTestRule.onNodeWithTag(TestTag.VisibleWhileAppIsOpened).onListItemSwitch()
            .assertIsOn()
        //visible while app is saved
        newViewModel = MicrophoneOverlaySettingsViewModel(get())
        assertTrue { newViewModel.viewState.value.isMicrophoneOverlayWhileAppEnabled }

    }
}