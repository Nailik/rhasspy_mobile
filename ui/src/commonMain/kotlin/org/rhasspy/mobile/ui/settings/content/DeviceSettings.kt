package org.rhasspy.mobile.ui.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.list.InformationListElement
import org.rhasspy.mobile.ui.content.list.SliderListItem
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.settings.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.DeviceSettings
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel

/**
 * Device Settings
 * Volume
 * HotWord on/off
 * AudioOutput on/off
 * IntentHandling on/off
 */

@Composable
fun DeviceSettingsContent() {
    val viewModel: DeviceSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier.testTag(DeviceSettings),
        title = MR.strings.device.stable,
        onBackClick = { viewModel.onEvent(DeviceSettingsUiEvent.Action.BackClick) }
    ) {

        InformationListElement(text = MR.strings.deviceSettingsLongInformation.stable)

        //volume slider
        SliderListItem(
            modifier = Modifier.testTag(TestTag.Volume),
            text = MR.strings.volume.stable,
            value = viewState.volume,
            onValueChange = { viewModel.onEvent(UpdateVolume(it)) }
        )

        //hot word enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.HotWord),
            text = MR.strings.hotWord.stable,
            isChecked = viewState.isHotWordEnabled,
            onCheckedChange = { viewModel.onEvent(SetHotWordEnabled(it)) }
        )

        //audio output enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.AudioOutput),
            text = MR.strings.audioOutput.stable,
            isChecked = viewState.isAudioOutputEnabled,
            onCheckedChange = { viewModel.onEvent(SetAudioOutputEnabled(it)) }
        )

        //intent handling enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.IntentHandling),
            text = MR.strings.intentHandling.stable,
            isChecked = viewState.isIntentHandlingEnabled,
            onCheckedChange = { viewModel.onEvent(SetIntentHandlingEnabled(it)) }
        )

    }

}