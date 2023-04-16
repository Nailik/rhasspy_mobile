package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel

/**
 * Log settings
 */
@Preview
@Composable
fun LogSettingsContent(viewModel: LogSettingsViewModel = get()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.LogSettings),
        title = MR.strings.logSettings.stable
    ) {

        //log level
        RadioButtonsEnumSelection(
            selected = viewModel.logLevel.collectAsState().value,
            onSelect = viewModel::selectLogLevel,
            values = viewModel.logLevelOptions
        )

        //crashlytics
        SwitchListItem(
            text = MR.strings.crashlytics.stable,
            secondaryText = MR.strings.crashlyticsText.stable,
            modifier = Modifier.testTag(TestTag.CrashlyticsEnabled),
            isChecked = viewModel.isCrashlyticsEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleCrashlyticsEnabled
        )

        //show log enabled
        SwitchListItem(
            text = MR.strings.showLog.stable,
            modifier = Modifier.testTag(TestTag.ShowLogEnabled),
            isChecked = viewModel.isShowLogEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleShowLogEnabled
        )

        //audio frames logging enabled
        SwitchListItem(
            text = MR.strings.audioFramesLogging.stable,
            modifier = Modifier.testTag(TestTag.AudioFramesEnabled),
            isChecked = viewModel.isLogAudioFramesEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleLogAudioFramesEnabled
        )

    }

}