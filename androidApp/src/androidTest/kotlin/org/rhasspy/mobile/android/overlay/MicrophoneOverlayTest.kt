package org.rhasspy.mobile.android.overlay

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithCombinedTag
import org.rhasspy.mobile.android.utils.requestMicrophonePermissions
import org.rhasspy.mobile.android.utils.requestOverlayPermissions
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag

@RunWith(AndroidJUnit4::class)
class MicrophoneOverlayTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        get<MicrophonePermission>().requestMicrophonePermissions()
        device.requestOverlayPermissions(composeTestRule.activity)
        AppSetting.microphoneOverlaySizeOption.value = MicrophoneOverlaySizeOption.Big
        AppSetting.isMicrophoneOverlayWhileAppEnabled.value = true
    }

    @Test
    fun test() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithCombinedTag(TestTag.MicrophoneFab, TestTag.Overlay).performClick()
        //text field changed text
        /*composeTestRule.waitUntil(
            condition = { get<DialogManagerService>().currentDialogState.value != DialogManagerServiceState.AwaitingWakeWord },
            timeoutMillis = 50000
        )
        assertNotEquals(DialogManagerServiceState.AwaitingWakeWord, get<DialogManagerService>().currentDialogState.value)*/
    }

}