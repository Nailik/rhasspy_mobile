package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.SpeechToTextConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import kotlin.test.assertEquals

class SpeechToTextConfigurationContentTest : FlakyTest() {

    private val viewModel = get<SpeechToTextConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        SpeechToTextConfigurationScreen()
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
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectSpeechToTextOption(SpeechToTextOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(SpeechToTextOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(SpeechToTextOption.RemoteHTTP).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            SpeechToTextOption.RemoteHTTP,
            viewModel.viewState.value.editData.speechToTextOption
        )

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).assertExists()

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
        assertEquals(textInputTest, viewModel.viewState.value.editData.speechToTextHttpEndpoint)

        //User clicks save
        composeTestRule.saveBottomAppBar()
        SpeechToTextConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(SpeechToTextOption.RemoteHTTP, it.speechToTextOption)
            //endpoint is saved
            assertEquals(textInputTest, it.speechToTextHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomSpeechToTextHttpEndpoint)
        }
    }

}