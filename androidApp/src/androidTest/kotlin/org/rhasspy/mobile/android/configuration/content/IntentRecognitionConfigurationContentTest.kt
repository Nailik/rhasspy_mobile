package org.rhasspy.mobile.android.configuration.content

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
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.viewModels.configuration.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewModels.configuration.IntentRecognitionConfigurationViewModel
import kotlin.test.assertEquals

class IntentRecognitionConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = IntentRecognitionConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                IntentRecognitionConfigurationContent(viewModel)
            }
        }

    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * Endpoint visible
     * custom endpoint switch visible
     *
     * switch is off
     * endpoint cannot be changed
     *
     * user clicks switch
     * switch is on
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     * use custom endpoint is saved
     */
    @Test
    fun testEndpoint() = runBlocking {
        viewModel.selectIntentRecognitionOption(IntentRecognitionOptions.Disabled)
        viewModel.save()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(IntentRecognitionOptions.Disabled, true).onChildAt(0).assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentRecognitionOptions.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(IntentRecognitionOptions.RemoteHTTP, viewModel.intentRecognitionOption.value)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).onChild().assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).onChild().assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint, true).onChild().performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewModel.intentRecognitionHttpEndpoint.value)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = IntentRecognitionConfigurationViewModel()
        //option is saved to remote http
        assertEquals(IntentRecognitionOptions.RemoteHTTP, newViewModel.intentRecognitionOption.value)
        //endpoint is saved
        assertEquals(textInputTest, newViewModel.intentRecognitionHttpEndpoint.value)
        //use custom endpoint is saved
        assertEquals(true, newViewModel.isUseCustomIntentRecognitionHttpEndpoint.value)
    }

}