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
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.ui.configuration.IntentRecognitionConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import kotlin.test.assertEquals

class IntentRecognitionConfigurationContentTest : FlakyTest() {

    private val viewModel = get<IntentRecognitionConfigurationViewModel>()

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

        viewModel.onEvent(SelectIntentRecognitionOption(IntentRecognitionOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(IntentRecognitionOption.Disabled, true)
            .onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(IntentRecognitionOption.Rhasspy2HermesHttp).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            IntentRecognitionOption.Rhasspy2HermesHttp,
            viewModel.viewState.value.editData.intentRecognitionOption
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        IntentRecognitionConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(IntentRecognitionOption.Rhasspy2HermesHttp, it.intentRecognitionOption)
        }
    }

}