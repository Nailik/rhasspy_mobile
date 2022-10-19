package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.viewModels.configuration.DialogManagementConfigurationViewModel


/**
 * DropDown to select dialog management option
 */
@Preview
@Composable
fun DialogManagementConfigurationContent(viewModel: DialogManagementConfigurationViewModel = viewModel()) {

    PageContent(MR.strings.dialogueManagement) {

        //drop down to select option
        DropDownEnumListItem(
            selected = viewModel.dialogueManagementOption.collectAsState().value,
            onSelect = viewModel::selectDialogManagementOption,
            values = viewModel.dialogManagementOptionsList
        )

    }

}
