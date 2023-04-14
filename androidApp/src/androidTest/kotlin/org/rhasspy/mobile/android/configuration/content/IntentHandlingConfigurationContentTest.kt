package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemRadioButton
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState
import kotlin.test.assertEquals

class IntentHandlingConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = IntentHandlingConfigurationViewModel(
        service = DialogManagerService(),
        testRunner = IntentHandlingConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                IntentHandlingConfigurationContent(viewModel)
            }
        }

    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * Endpoint visible
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     * use custom endpoint is saved
     */
    @Test
    fun testEndpoint() = runBlocking {
        viewModel.onAction(SelectIntentHandlingOption(IntentHandlingOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"
        //option disable is set
        composeTestRule.onNodeWithTag(IntentHandlingOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentHandlingOption.RemoteHTTP, true).performClick()
        //new option is selected
        assertEquals(IntentHandlingOption.RemoteHTTP, viewState.value.intentHandlingOption)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint, true).assertExists()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.intentHandlingHttpEndpoint)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = IntentHandlingConfigurationViewState()
        //option is saved to remote http
        assertEquals(IntentHandlingOption.RemoteHTTP, newViewState.intentHandlingOption)
        //endpoint is saved
        assertEquals(textInputTest, newViewState.intentHandlingHttpEndpoint)
    }

    /**
     * option disable is set
     * User clicks option HomeAssistant
     * new option is selected
     *
     * endpoint visible
     * access token visible
     * send events visible
     * send intents visible
     *
     * endpoint can be changed
     * access token can be changed
     *
     * send intents is set
     * send events can clicked
     * send events is set
     *
     * User clicks save
     * option is saved to HomeAssistant
     * endpoint is saved
     * access token is saved
     * send events is saved
     */
    @Test
    fun testHomeAssistant() = runBlocking {
        viewModel.onAction(SelectIntentHandlingOption(IntentHandlingOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTestEndpoint = "endpointTestInput"
        val textInputTestToken = "tokenTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(IntentHandlingOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option HomeAssistant
        composeTestRule.onNodeWithTag(IntentHandlingOption.HomeAssistant).performClick()
        //new option is selected
        assertEquals(IntentHandlingOption.HomeAssistant, viewState.value.intentHandlingOption)

        //endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //access token visible
        composeTestRule.onNodeWithTag(TestTag.AccessToken).assertExists()
        //send events visible
        composeTestRule.onNodeWithTag(TestTag.SendEvents).assertExists()
        //send intents visible
        composeTestRule.onNodeWithTag(TestTag.SendIntents).assertExists()

        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Endpoint)
            .performTextReplacement(textInputTestEndpoint)
        //access token can be changed
        composeTestRule.onNodeWithTag(TestTag.AccessToken).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.AccessToken)
            .performTextReplacement(textInputTestToken)

        //send intents is set
        composeTestRule.onNodeWithTag(TestTag.SendIntents, true)
            .performScrollTo().performClick().onListItemRadioButton().assertIsSelected()
        //send events can clicked
        composeTestRule.onNodeWithTag(TestTag.SendEvents).performScrollTo().performClick()
        //send events is set
        composeTestRule.onNodeWithTag(TestTag.SendEvents, true)
            .performScrollTo().onListItemRadioButton().assertIsSelected()

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = IntentHandlingConfigurationViewState()
        //option is saved to HomeAssistant
        assertEquals(IntentHandlingOption.HomeAssistant, newViewState.intentHandlingOption)
        //endpoint is saved
        assertEquals(textInputTestEndpoint, newViewState.intentHandlingHassEndpoint)
        //access token is saved
        assertEquals(textInputTestToken, newViewState.intentHandlingHassAccessToken)
        //send events is saved
        assertEquals(HomeAssistantIntentHandlingOption.Event, newViewState.intentHandlingHassOption)
    }

}