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
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationScreen(viewModel: DialogManagementConfigurationViewModel) {

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
    editData: DialogManagementConfigurationData,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {

    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = editData.dialogManagementOption,
        onSelect = { onEvent(SelectDialogManagementOption(it)) },
        values = editData.dialogManagementOptionList
    )

}