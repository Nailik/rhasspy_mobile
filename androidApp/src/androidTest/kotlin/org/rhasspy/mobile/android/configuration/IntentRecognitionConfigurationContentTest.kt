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
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import org.rhasspy.mobile.ui.configuration.domains.intent.IntentRecognitionConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import kotlin.test.assertEquals

class IntentRecognitionConfigurationContentTest : FlakyTest() {

    private val viewModel = get<IntentDomainConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        IntentRecognitionConfigurationScreen(viewModel)
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

        viewModel.onEvent(SelectIntentRecognitionOption(IntentDomainOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(IntentDomainOption.Disabled, true)
            .onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentDomainOption.Rhasspy2HermesHttp).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            IntentDomainOption.Rhasspy2HermesHttp,
            viewModel.viewState.value.editData.intentDomainOption
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        IntentDomainConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(IntentDomainOption.Rhasspy2HermesHttp, it.intentDomainOption)
        }
    }

}