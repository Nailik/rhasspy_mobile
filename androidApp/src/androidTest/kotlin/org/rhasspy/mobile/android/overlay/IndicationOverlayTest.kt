package org.rhasspy.mobile.android.overlay

import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.main.MainScreen

@RunWith(AndroidJUnit4::class)
class IndicationOverlayTest : FlakyTest() {

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Composable
    override fun ComposableContent() {
        MainScreen(LocalViewModelFactory.current)
    }

    @Test
    @AllowFlaky
    fun test() {
        setupContent()
        device.requestOverlayPermissions(activity, get())
        AppSetting.isWakeWordLightIndicationEnabled.value = true

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