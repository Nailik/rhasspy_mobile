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
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.ui.configuration.pipeline.DialogManagementConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Change.SelectPipelineOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewModel
import kotlin.test.assertEquals

class DialogManagementConfigurationContentTest : FlakyTest() {

    private val viewModel = get<PipelineConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        DialogManagementConfigurationScreen(viewModel)
    }

    /**
     * option disable is set
     * User clicks option local
     * new option is selected
     *
     * User clicks save
     * option is saved to local
     */
    @Test
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectPipelineOption(PipelineManagerOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(PipelineManagerOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()
        //User clicks option local
        composeTestRule.onNodeWithTag(PipelineManagerOption.Local).performClick()
        composeTestRule.awaitIdle()
        //new option is selected
        composeTestRule.onNodeWithTag(PipelineManagerOption.Local, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        PipelineConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to local
            assertEquals(PipelineManagerOption.Local, it.pipelineManagerOption)
        }
    }

}