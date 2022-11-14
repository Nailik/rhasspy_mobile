package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.settings.LogSettingsViewModel

/**
 * Log settings
 */
@Preview
@Composable
fun LogSettingsContent(viewModel: LogSettingsViewModel = viewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.LogSettings),
        title = MR.strings.logSettings) {

        //log level
        RadioButtonsEnumSelection(
            selected = viewModel.logLevel.collectAsState().value,
            onSelect = viewModel::selectLogLevel,
            values = viewModel.logLevelOptions
        )

        //show log enabled
        SwitchListItem(
            text = MR.strings.showLog,
            modifier = Modifier.testTag(TestTag.ShowLogEnabled),
            isChecked = viewModel.isShowLogEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleShowLogEnabled
        )

        //audio frames logging enabled
        SwitchListItem(
            text = MR.strings.audioFramesLogging,
            modifier = Modifier.testTag(TestTag.AudioFramesEnabled),
            isChecked = viewModel.isLogAudioFramesEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleLogAudioFramesEnabled
        )

    }

}