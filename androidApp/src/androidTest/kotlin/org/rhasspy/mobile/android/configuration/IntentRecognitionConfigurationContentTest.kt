package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.IntentRecognitionConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import kotlin.test.assertEquals

class IntentRecognitionConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<IntentRecognitionConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                IntentRecognitionConfigurationScreen()
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
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(IntentRecognitionOption.Disabled, true)
            .onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentRecognitionOption.RemoteHTTP).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            IntentRecognitionOption.RemoteHTTP,
            viewModel.viewState.value.editData.intentRecognitionOption
        )

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo()
            .onListItemSwitch()
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
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextInput(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(
            textInputTest,
            viewModel.viewState.value.editData.intentRecognitionHttpEndpoint
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        IntentRecognitionConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(IntentRecognitionOption.RemoteHTTP, it.intentRecognitionOption)
            //endpoint is saved
            assertEquals(textInputTest, it.intentRecognitionHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomIntentRecognitionHttpEndpoint)
        }
    }

}