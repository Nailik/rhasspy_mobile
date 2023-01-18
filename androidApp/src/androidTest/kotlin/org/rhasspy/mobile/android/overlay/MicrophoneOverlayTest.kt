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
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class MicrophoneOverlayTest : KoinComponent {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        requestMicrophonePermissions()
        device.requestOverlayPermissions()
        AppSetting.microphoneOverlaySizeOption.value = MicrophoneOverlaySizeOption.Big
        AppSetting.isMicrophoneOverlayWhileAppEnabled.value = true
    }

    @Test
    fun test() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithCombinedTag(TestTag.MicrophoneFab, TestTag.Overlay).performClick()
        //text field changed text
        composeTestRule.waitUntil(
            condition = { get<DialogManagerService>().currentDialogState.value != DialogManagerServiceState.AwaitingWakeWord },
            timeoutMillis = 5000
        )
        assertNotEquals(DialogManagerServiceState.AwaitingWakeWord, get<DialogManagerService>().currentDialogState.value)
    }
}