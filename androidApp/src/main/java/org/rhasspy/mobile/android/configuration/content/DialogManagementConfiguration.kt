package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.viewModels.configuration.DialogManagementConfigurationViewModel

/**
 * DropDown to select dialog management option
 */
@Preview
@Composable
fun DialogManagementConfigurationContent(viewModel: DialogManagementConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.DialogManagementConfiguration),
        title = MR.strings.dialogManagement,
        viewModel = viewModel
    ) {

        //drop down to select option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.DialogManagementOptions),
            selected = viewModel.dialogManagementOption.collectAsState().value,
            onSelect = viewModel::selectDialogManagementOption,
            values = viewModel.dialogManagementOptionsList
        )

    }

}