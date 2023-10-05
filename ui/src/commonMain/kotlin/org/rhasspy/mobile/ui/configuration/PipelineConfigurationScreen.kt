package org.rhasspy.mobile.ui.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Change.SelectPipelineOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationScreen(viewModel: PipelineConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.dialog_pipeline.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        DialogManagementEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun DialogManagementEditContent(
    editData: PipelineConfigurationData,
    onEvent: (PipelineConfigurationUiEvent) -> Unit
) {

    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = editData.pipelineManagerOption,
        onSelect = { onEvent(SelectPipelineOption(it)) },
        values = editData.pipelineManagerOptionList
    )

}