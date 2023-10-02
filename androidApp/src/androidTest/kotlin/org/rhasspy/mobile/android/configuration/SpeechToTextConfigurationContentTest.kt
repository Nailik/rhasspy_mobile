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
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import org.rhasspy.mobile.ui.configuration.audioinput.SpeechToTextConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change.SelectAsrOption
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import kotlin.test.assertEquals

class SpeechToTextConfigurationContentTest : FlakyTest() {

    private val viewModel = get<AsrConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        SpeechToTextConfigurationScreen(viewModel)
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

        viewModel.onEvent(SelectAsrOption(AsrDomainOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(AsrDomainOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AsrDomainOption.Rhasspy2HermesHttp).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            AsrDomainOption.Rhasspy2HermesHttp,
            viewModel.viewState.value.editData.asrDomainOption
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        AsrConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(AsrDomainOption.Rhasspy2HermesHttp, it.asrDomainOption)
        }
    }

}