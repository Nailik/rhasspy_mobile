package org.rhasspy.mobile.android.configuration.content

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.edit.TextToSpeechEditConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import kotlin.test.assertEquals

class TextToSpeechConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<TextToSpeechConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                TextToSpeechEditConfigurationScreen()
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
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectTextToSpeechOption(TextToSpeechOption.Disabled))
        viewModel.onAction(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(TextToSpeechOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(TextToSpeechOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(TextToSpeechOption.RemoteHTTP, viewState.value.textToSpeechOption)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().onListItemSwitch().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.textToSpeechHttpEndpoint)

        //User clicks save
        composeTestRule.saveBottomAppBar(viewModel)
        TextToSpeechConfigurationViewModel(get()).viewState.value.editViewState.value.also {
            //option is saved to remote http
            assertEquals(TextToSpeechOption.RemoteHTTP, it.textToSpeechOption)
            //endpoint is saved
            assertEquals(textInputTest, it.textToSpeechHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomTextToSpeechHttpEndpoint)
        }
    }

}