package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.TextToSpeechConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import kotlin.test.assertEquals

class TextToSpeechConfigurationContentTest : FlakyTest() {

    private val viewModel = get<TextToSpeechConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        TextToSpeechConfigurationScreen()
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

        viewModel.onEvent(SelectTextToSpeechOption(TextToSpeechOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(TextToSpeechOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(TextToSpeechOption.RemoteHTTP).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            TextToSpeechOption.RemoteHTTP,
            viewModel.viewState.value.editData.textToSpeechOption
        )

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo()
            .onListItemSwitch().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextInput(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewModel.viewState.value.editData.textToSpeechHttpEndpoint)

        //User clicks save
        composeTestRule.saveBottomAppBar()
        TextToSpeechConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(TextToSpeechOption.RemoteHTTP, it.textToSpeechOption)
            //endpoint is saved
            assertEquals(textInputTest, it.textToSpeechHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomTextToSpeechHttpEndpoint)
        }
    }

}