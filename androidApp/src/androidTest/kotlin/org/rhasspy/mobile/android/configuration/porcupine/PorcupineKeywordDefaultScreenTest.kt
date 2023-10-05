package org.rhasspy.mobile.android.configuration.porcupine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.domains.wake.porcupine.PorcupineKeywordDefaultScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewModel
import kotlin.test.assertFalse

class PorcupineKeywordDefaultScreenTest : FlakyTest() {

    private val viewModel = get<WakeDomainConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        val viewState by viewModel.viewState.collectAsState()
        PorcupineKeywordDefaultScreen(
            editData = viewState.editData.wakeWordPorcupineConfigurationData,
            onEvent = viewModel::onEvent
        )
    }

    /**
     * no wake word is set
     * none is selected
     *
     * user clicks americano
     * americano is selected
     * sensitivity is shown
     *
     * user clicks porcupine
     * porcupine is selected
     * sensitivity is shown
     *
     * user clicks porcupine
     * porcupine is unselected
     * sensitivity is not shown
     *
     * viewModel save is invoked
     * americano is saved with enabled
     * porcupine is saved with not enabled
     * everything else is saved with not enabled
     */
    @Test
    @AllowFlaky
    fun testList() = runTest {
        setupContent()

        //no wake word is set
        val viewState = viewModel.viewState.value.editData.wakeWordPorcupineConfigurationData
        viewState.defaultOptionsUi.forEach {
            assertFalse(it.isEnabled)
        }
        //none is selected
        viewState.defaultOptionsUi.forEach {
            composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
                .performScrollToNode(hasTestTag(it.option))
            composeTestRule.onNodeWithTag(it.option).onListItemSwitch().assertIsOff()
            composeTestRule.awaitIdle()
        }

        //user clicks americano
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToNode(hasTestTag(PorcupineKeywordOption.AMERICANO))
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.AMERICANO).performClick()
        composeTestRule.awaitIdle()
        //americano is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.AMERICANO).onListItemSwitch()
            .assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.AMERICANO,
            TestTag.Sensitivity
        ).assertIsDisplayed()

        //user clicks porcupine

        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToNode(hasTestTag(PorcupineKeywordOption.PORCUPINE))
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).onListItemSwitch()
            .assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.PORCUPINE,
            TestTag.Sensitivity
        ).assertIsDisplayed()

        //user clicks porcupine
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToNode(hasTestTag(PorcupineKeywordOption.PORCUPINE))
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is unselected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).onListItemSwitch()
            .assertIsOff()
        //sensitivity is not shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.PORCUPINE,
            TestTag.Sensitivity
        ).assertDoesNotExist()

        //viewModel save is invoked
        viewModel.onEvent(Save)
    }
}