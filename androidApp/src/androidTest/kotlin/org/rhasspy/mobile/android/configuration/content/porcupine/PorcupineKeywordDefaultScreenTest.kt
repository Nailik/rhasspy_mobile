package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithCombinedTag
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.PorcupineKeywordOptions
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel
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
                .onChildAt(0)
                .onChildAt(0)
                .assertIsOff()
            composeTestRule.awaitIdle()
        }

        //user clicks americano
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOptions.AMERICANO)
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.AMERICANO).performClick()
        composeTestRule.awaitIdle()
        //americano is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.AMERICANO).onChildAt(0).assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(PorcupineKeywordOptions.AMERICANO, TestTag.Sensitivity).assertIsDisplayed()

        //user clicks porcupine

        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOptions.PORCUPINE)
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is selected
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.PORCUPINE).onChildAt(0).assertIsOn()
        //sensitivity is shown
        composeTestRule.onNodeWithCombinedTag(PorcupineKeywordOptions.PORCUPINE, TestTag.Sensitivity).assertIsDisplayed()

        //user clicks porcupine
        composeTestRule
            .onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen)
            .performScrollToKey(PorcupineKeywordOptions.PORCUPINE)
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.PORCUPINE).performClick()
        composeTestRule.awaitIdle()
        //porcupine is unselected
        composeTestRule.onNodeWithTag(PorcupineKeywordOptions.PORCUPINE).onChildAt(0).assertIsOff()
        //sensitivity is not shown
        composeTestRule.onNodeWithCombinedTag(PorcupineKeywordOptions.PORCUPINE, TestTag.Sensitivity).assertDoesNotExist()

        //viewModel save is invoked
        viewModel.save()
        val newViewModel = WakeWordConfigurationViewModel()
        newViewModel.wakeWordPorcupineKeywordDefaultOptions.value.forEach {
            //americano is saved with enabled
            //porcupine is saved with not enabled
            //everything else is saved with not enabled
            if (it.option == PorcupineKeywordOptions.AMERICANO) {
                assertTrue(it.isEnabled)
            } else {
                assertFalse(it.isEnabled)
            }
        }
    }
}