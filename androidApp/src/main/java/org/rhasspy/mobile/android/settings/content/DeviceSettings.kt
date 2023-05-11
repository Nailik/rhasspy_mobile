package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.*

/**
 * Device Settings
 * Volume
 * HotWord on/off
 * AudioOutput on/off
 * IntentHandling on/off
 */
@Preview
@Composable
fun DeviceSettingsContent() {
    val viewModel: DeviceSettingsSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.DeviceSettings),
        title = MR.strings.device.stable
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