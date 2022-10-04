package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun ShowLogSettingsItem(viewModel: SettingsScreenViewModel) {
    val logLevel by viewModel.logLevel.collectAsState()

    ExpandableListItem(
        text = MR.strings.logSettings,
        secondaryText = logLevel.text
    ) {
        DropDownEnumListItem(
            selected = logLevel,
            onSelect = viewModel::updateLogLevel,
            values = LogLevel::values
        )

        SwitchListItem(
            MR.strings.showLog,
            isChecked = viewModel.isShowLogEnabled.collectAsState().value,
            onCheckedChange = viewModel::updateShowLogEnabled
        )

        SwitchListItem(
            MR.strings.audioFramesLogging,
            isChecked = viewModel.isLogAudioFramesEnabled.collectAsState().value,
            onCheckedChange = viewModel::updateLogAudioFramesEnabled
        )
    }
}