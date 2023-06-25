package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.IntentHandlingConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingOption
import kotlin.test.assertEquals

class IntentHandlingConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<IntentHandlingConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                IntentHandlingConfigurationScreen()
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
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectIntentHandlingOption(IntentHandlingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val editData = viewModel.viewState.value.editData

        val textInputTest = "endpointTestInput"
        //option disable is set
        composeTestRule.onNodeWithTag(IntentHandlingOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentHandlingOption.RemoteHTTP, true).performClick()
        //new option is selected
        assertEquals(IntentHandlingOption.RemoteHTTP, editData.intentHandlingOption)
        composeTestRule.awaitIdle()

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint, true).assertExists()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, editData.intentHandlingHttpEndpoint)

        composeTestRule.onNodeWithTag(IntentHandlingOption.RemoteHTTP, true).performClick()

        //User clicks save
        composeTestRule.saveBottomAppBar(viewModel)
        IntentHandlingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(IntentHandlingOption.RemoteHTTP, it.intentHandlingOption)
            //endpoint is saved
            assertEquals(textInputTest, it.intentHandlingHttpEndpoint)
        }
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
    fun testHomeAssistant() = runTest {
        viewModel.onEvent(SelectIntentHandlingOption(IntentHandlingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val editData = viewModel.viewState.value.editData

        val textInputTestEndpoint = "endpointTestInput"
        val textInputTestToken = "tokenTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(IntentHandlingOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option HomeAssistant
        composeTestRule.onNodeWithTag(IntentHandlingOption.HomeAssistant).performClick()
        //new option is selected
        assertEquals(IntentHandlingOption.HomeAssistant, editData.intentHandlingOption)

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
        composeTestRule.saveBottomAppBar(viewModel)
        IntentHandlingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to HomeAssistant
            assertEquals(IntentHandlingOption.HomeAssistant, it.intentHandlingOption)
            //endpoint is saved
            assertEquals(textInputTestEndpoint, it.intentHandlingHomeAssistantEndpoint)
            //access token is saved
            assertEquals(textInputTestToken, it.intentHandlingHomeAssistantAccessToken)
            //send events is saved
            assertEquals(HomeAssistantIntentHandlingOption.Event, it.intentHandlingHomeAssistantOption)
        }
    }

}