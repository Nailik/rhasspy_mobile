package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithCombinedTag
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.data.serviceoption.PorcupineKeywordOption
import org.rhasspy.mobile.viewmodel.configuration.WakeWordConfigurationViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PorcupineKeywordDefaultScreenTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                PorcupineKeywordDefaultScreen(viewModel)
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
    fun testList() = runBlocking {
        //no wake word is set
        viewModel.wakeWordPorcupineKeywordDefaultOptions.value.forEach {
            assertFalse(it.isEnabled)
        }
        //none is selected
        viewModel.wakeWordPorcupineKeywordDefaultOptions.value.forEach {
            composeTestRule
                .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen, true)
                .performScrollToKey(it.option)
                .onListItemSwitch()
                .assertIsOff()
            composeTestRule.awaitIdle()
        }

        //user clicks americano
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOption.AMERICANO)
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.AMERICANO).performClick()
        composeTestRule.awaitIdle()
        //americano is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.AMERICANO).onListItemSwitch().assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.AMERICANO,
            TestTag.Sensitivity
        ).assertIsDisplayed()

        //user clicks porcupine

        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOption.PORCUPINE)
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).onListItemSwitch().assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.PORCUPINE,
            TestTag.Sensitivity
        ).assertIsDisplayed()

        //user clicks porcupine
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOption.PORCUPINE)
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is unselected
        composeTestRule.onNodeWithTag(PorcupineKeywordOption.PORCUPINE).onListItemSwitch().assertIsOff()
        //sensitivity is not shown
        composeTestRule.onNodeWithCombinedTag(
            PorcupineKeywordOption.PORCUPINE,
            TestTag.Sensitivity
        ).assertDoesNotExist()

        //viewModel save is invoked
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        val newViewModel = WakeWordConfigurationViewModel()
        newViewModel.wakeWordPorcupineKeywordDefaultOptions.value.forEach {
            //americano is saved with enabled
            //porcupine is saved with not enabled
            //everything else is saved with not enabled
            if (it.option == PorcupineKeywordOption.AMERICANO) {
                assertTrue(it.isEnabled)
            } else {
                assertFalse(it.isEnabled)
            }
        }
    }
}