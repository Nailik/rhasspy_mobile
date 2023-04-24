package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PorcupineKeywordScreenTest : KoinComponent {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<WakeWordConfigurationViewModel>()

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
                    onEvent = viewModel::onEvent
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
    fun testPager() = runTest {
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