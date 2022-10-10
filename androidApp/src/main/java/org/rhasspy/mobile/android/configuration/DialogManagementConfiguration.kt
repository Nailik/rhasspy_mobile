package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.BottomSheetScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.data.DialogueManagementOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
fun DialogManagementConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = viewModel.dialogueManagementOption.flow.collectAsState().value.text,
        screen = BottomSheetScreens.DialogueManagement
    )

}

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.dialogueManagement) {

        DropDownEnumListItem(
            selected = viewModel.dialogueManagementOption.flow.collectAsState().value,
            onSelect = viewModel.dialogueManagementOption::set,
            values = DialogueManagementOptions::values
        )

    }

}
