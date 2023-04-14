package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import kotlin.test.assertTrue

class PorcupineKeywordScreenTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel(
        service = WakeWordService(),
        testRunner = WakeWordConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                val viewState by viewModel.viewState.collectAsState()
                val contentViewState by viewState.editViewState.collectAsState()
                PorcupineKeywordScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onAction = viewModel::onAction
                )
            }
        }

    }

    /**
     * default is opened
     *
     * user slides to right
     * custom is opened and selected
     *
     * user slides to left
     * default is opened and selected
     *
     * user clicks custom
     * custom is opened and selected
     *
     * user clicks default
     * default is opened and selected
     */
    @Test
    fun testPager() = runBlocking {
        //default is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen).assertIsDisplayed()

        //user slides to right
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).performTouchInput {
            swipeLeft()
        }
        //custom is opened and selected
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTag.TabCustom).assertIsSelected()

        //user slides to left
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).performTouchInput {
            swipeRight()
        }
        //default is opened and selected
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTag.TabDefault).assertIsSelected()

        //user clicks custom
        composeTestRule.onNodeWithTag(TestTag.TabCustom).performClick()
        //custom is opened and selected
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTag.TabCustom).assertIsSelected()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.TabDefault).performClick()
        //default is opened and selected
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordDefaultScreen).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTag.TabDefault).assertIsSelected()

        assertTrue(true)
    }
}