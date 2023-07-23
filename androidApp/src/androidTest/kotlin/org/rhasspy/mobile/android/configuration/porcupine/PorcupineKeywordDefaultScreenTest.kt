package org.rhasspy.mobile.android.configuration.porcupine

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithCombinedTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordDefaultScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import kotlin.test.assertFalse

class PorcupineKeywordDefaultScreenTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<WakeWordConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                val viewState by viewModel.viewState.collectAsState()
                PorcupineKeywordDefaultScreen(
                    editData = viewState.editData.wakeWordPorcupineConfigurationData,
                    onEvent = viewModel::onEvent
                )
            }
        }

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
    fun testList() = runTest {
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