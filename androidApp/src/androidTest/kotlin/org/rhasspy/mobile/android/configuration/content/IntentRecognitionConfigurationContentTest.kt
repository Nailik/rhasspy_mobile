package org.rhasspy.mobile.android.configuration.content

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.content.IntentRecognitionConfigurationContent
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationEditViewModel
import kotlin.test.assertEquals

class IntentRecognitionConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<IntentRecognitionConfigurationEditViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                IntentRecognitionConfigurationContent()
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
        viewModel.onEvent(SelectIntentRecognitionOption(IntentRecognitionOption.Disabled))
        viewModel.onAction(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(IntentRecognitionOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentRecognitionOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(IntentRecognitionOption.RemoteHTTP, viewState.value.intentRecognitionOption)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().onListItemSwitch()
            .assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.intentRecognitionHttpEndpoint)

        //User clicks save
        composeTestRule.saveBottomAppBar(viewModel)
        IntentRecognitionConfigurationEditViewModel(get()).viewState.value.editViewState.value.also {
            //option is saved to remote http
            assertEquals(IntentRecognitionOption.RemoteHTTP, it.intentRecognitionOption)
            //endpoint is saved
            assertEquals(textInputTest, it.intentRecognitionHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomIntentRecognitionHttpEndpoint)
        }
    }

}