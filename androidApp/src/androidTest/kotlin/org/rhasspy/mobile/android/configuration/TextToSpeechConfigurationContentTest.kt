package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.ui.configuration.TextToSpeechConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import kotlin.test.assertEquals

class TextToSpeechConfigurationContentTest : FlakyTest() {

    private val viewModel = get<TextToSpeechConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        TextToSpeechConfigurationScreen(viewModel)
    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * User clicks save
     * option is saved to remote http
     */
    @Test
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectTextToSpeechOption(TextToSpeechOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

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

        //User clicks save
        composeTestRule.saveBottomAppBar()
        TextToSpeechConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(TextToSpeechOption.RemoteHTTP, it.textToSpeechOption)
        }
    }

}