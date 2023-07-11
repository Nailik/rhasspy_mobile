package org.rhasspy.mobile.ui.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.settings.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.LogSettings
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel

/**
 * Log settings
 */
@Composable
fun LogSettingsContent() {
    val viewModel: LogSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        SettingsScreenItemContent(
            modifier = Modifier.testTag(LogSettings),
            title = MR.strings.logSettings.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
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

}