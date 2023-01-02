package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.configuration.DialogManagementConfigurationViewModel

/**
 * DropDown to select dialog management option
 */
@Preview
@Composable
fun DialogManagementConfigurationContent(viewModel: DialogManagementConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.DialogManagementConfiguration),
        title = MR.strings.dialogManagement,
        viewModel = viewModel
    ) {

        item {
            //drop down to select option
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.DialogManagementOptions),
                selected = viewModel.dialogManagementOption.collectAsState().value,
                onSelect = viewModel::selectDialogManagementOption,
                values = viewModel.dialogManagementOptionList
            )
        }

    }

}