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
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import org.rhasspy.mobile.ui.configuration.domains.TextToSpeechConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change.SelectTtsDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationViewModel
import kotlin.test.assertEquals

class TextToSpeechConfigurationContentTest : FlakyTest() {

    private val viewModel = get<TtsDomainConfigurationViewModel>()

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

        viewModel.onEvent(SelectTtsDomainOption(TtsDomainOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(TtsDomainOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(TtsDomainOption.Rhasspy2HermesHttp).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            TtsDomainOption.Rhasspy2HermesHttp,
            viewModel.viewState.value.editData.ttsDomainOption
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        TtsDomainConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(TtsDomainOption.Rhasspy2HermesHttp, it.ttsDomainOption)
        }
    }

}