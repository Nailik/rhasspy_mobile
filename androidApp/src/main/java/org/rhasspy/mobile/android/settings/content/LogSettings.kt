package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel

/**
 * Log settings
 */
@Preview
@Composable
fun LogSettingsContent() {
    val viewModel: LogSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.LogSettings),
        title = MR.strings.logSettings.stable
    ) {

        //log level
        RadioButtonsEnumSelectionList(
            selected = viewState.logLevel,
            onSelect = { viewModel.onEvent(SetLogLevel(it)) },
            values = viewState.logLevelOptions
        )

        //crashlytics
        SwitchListItem(
            text = MR.strings.crashlytics.stable,
            secondaryText = MR.strings.crashlyticsText.stable,
            modifier = Modifier.testTag(TestTag.CrashlyticsEnabled),
            isChecked = viewState.isCrashlyticsEnabled,
            onCheckedChange = { viewModel.onEvent(SetCrashlyticsEnabled(it)) }
        )

        //show log enabled
        SwitchListItem(
            text = MR.strings.showLog.stable,
            modifier = Modifier.testTag(TestTag.ShowLogEnabled),
            isChecked = viewState.isShowLogEnabled,
            onCheckedChange = { viewModel.onEvent(SetShowLogEnabled(it)) }
        )

        //audio frames logging enabled
        SwitchListItem(
            text = MR.strings.audioFramesLogging.stable,
            modifier = Modifier.testTag(TestTag.AudioFramesEnabled),
            isChecked = viewState.isLogAudioFramesEnabled,
            onCheckedChange = { viewModel.onEvent(SetLogAudioFramesEnabled(it)) }
        )

    }

}