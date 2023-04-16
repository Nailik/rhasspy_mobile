package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsSettingsViewModel

/**
 * Device Settings
 * Volume
 * HotWord on/off
 * AudioOutput on/off
 * IntentHandling on/off
 */
@Preview
@Composable
fun DeviceSettingsContent(viewModel: DeviceSettingsSettingsViewModel = get()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.DeviceSettings),
        title = MR.strings.device.stable
    ) {

        InformationListElement(text = MR.strings.deviceSettingsLongInformation.stable)

        //volume slider
        SliderListItem(
            modifier = Modifier.testTag(TestTag.Volume),
            text = MR.strings.volume.stable,
            value = viewModel.volume.collectAsState().value,
            onValueChange = viewModel::updateVolume
        )

        //hot word enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.HotWord),
            text = MR.strings.hotWord.stable,
            isChecked = viewModel.isHotWordEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHotWordEnabled
        )

        //audio output enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.AudioOutput),
            text = MR.strings.audioOutput.stable,
            isChecked = viewModel.isAudioOutputEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleAudioOutputEnabled
        )

        //intent handling enabled
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.IntentHandling),
            text = MR.strings.intentHandling.stable,
            isChecked = viewModel.isIntentHandlingEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleIntentHandlingEnabled
        )

    }

}