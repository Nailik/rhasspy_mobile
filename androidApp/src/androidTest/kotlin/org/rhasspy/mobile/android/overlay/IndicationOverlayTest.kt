package org.rhasspy.mobile.android.overlay

import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.hasCombinedTestTag
import org.rhasspy.mobile.android.utils.requestOverlayPermissions
import org.rhasspy.mobile.android.utils.waitUntilExists
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel

@RunWith(AndroidJUnit4::class)
class IndicationOverlayTest : FlakyTest() {

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val viewModel = get<MainScreenViewModel>()

    @Composable
    override fun ComposableContent() {
        MainScreen(viewModel)
    }

    @Test
    @AllowFlaky
    fun test() {
        setupContent()
        device.requestOverlayPermissions(activity, get())
        AppSetting.isWakeWordLightIndicationEnabled.value = true

        composeTestRule.waitForIdle()
        get<IIndication>().onThinking()
        composeTestRule.waitUntil(
            condition = { get<IIndication>().indicationState.value != IndicationState.Idle },
            timeoutMillis = 5000
        )
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(
            condition = { get<IIndication>().indicationState.value != IndicationState.Idle },
            timeoutMillis = 5000
        )
        composeTestRule.waitUntilExists(hasCombinedTestTag(TestTag.Indication, TestTag.Overlay))
    }
}