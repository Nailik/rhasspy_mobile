package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun ProblemHandlingSettingsItem(viewModel: SettingsScreenViewModel) {
    ExpandableListItem(
        text = MR.strings.problemHandling
    ) {
        SwitchListItem(
            text = MR.strings.forceCancel,
            secondaryText = MR.strings.forceCancelInformation,
            isChecked = viewModel.isForceCancelEnabled.collectAsState().value,
            onCheckedChange = viewModel::updateForceCancelEnabled)
    }
}