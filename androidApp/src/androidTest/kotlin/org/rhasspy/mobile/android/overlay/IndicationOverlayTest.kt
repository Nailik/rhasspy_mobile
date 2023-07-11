package org.rhasspy.mobile.android.overlay

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.get
import org.rhasspy.mobile.MainActivity
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.hasCombinedTestTag
import org.rhasspy.mobile.android.utils.requestOverlayPermissions
import org.rhasspy.mobile.android.utils.waitUntilExists
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag

@RunWith(AndroidJUnit4::class)
class IndicationOverlayTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        device.requestOverlayPermissions(composeTestRule.activity, get())
        AppSetting.isWakeWordLightIndicationEnabled.value = true
    }

    @Test
    fun test() {
        composeTestRule.waitForIdle()
        get<IIndicationService>().onThinking()
        composeTestRule.waitUntil(
            condition = { get<IIndicationService>().indicationState.value != IndicationState.Idle },
            timeoutMillis = 5000
        )
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(
            condition = { get<IIndicationService>().indicationState.value != IndicationState.Idle },
            timeoutMillis = 5000
        )
        composeTestRule.waitUntilExists(hasCombinedTestTag(TestTag.Indication, TestTag.Overlay))
    }
}