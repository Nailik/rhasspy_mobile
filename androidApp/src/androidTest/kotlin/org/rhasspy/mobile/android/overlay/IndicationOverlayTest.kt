package org.rhasspy.mobile.android.overlay

import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.indication.IndicationState
import org.rhasspy.mobile.settings.AppSetting


@RunWith(AndroidJUnit4::class)
class IndicationOverlayTest : KoinComponent {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        device.requestOverlayPermissions()
        AppSetting.isWakeWordLightIndicationEnabled.value = true
    }

    @Test
    fun test() {
        composeTestRule.waitForIdle()
        get<IndicationService>().onThinking()
        composeTestRule.waitUntil(
            condition = { get<IndicationService>().indicationState.value != IndicationState.Idle },
            timeoutMillis = 5000
        )
        composeTestRule.waitForIdle()
        device.waitForIdle()
        composeTestRule.onNodeWithCombinedTag(TestTag.Indication, TestTag.Overlay).assertExists()
    }
}