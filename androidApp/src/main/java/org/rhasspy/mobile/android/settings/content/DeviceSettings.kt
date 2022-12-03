package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.settings.DeviceSettingsSettingsViewModel

/**
 * Device Settings
 * Volume
 * HotWord on/off
 * AudioOutput on/off
 * IntentHandling on/off
 */
@Preview
@Composable
fun DeviceSettingsContent(viewModel: DeviceSettingsSettingsViewModel = getViewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.DeviceSettings),
        title = MR.strings.device
    ) {

        Column {

            //volume slider
            SliderListItem(
                modifier = Modifier.testTag(TestTag.Volume),
                text = MR.strings.volume,
                value = viewModel.volume.collectAsState().value,
                onValueChange = viewModel::updateVolume
            )

            //hot word enabled
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.HotWord),
                text = MR.strings.hotWord,
                isChecked = viewModel.isHotWordEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHotWordEnabled
            )

            //audio output enabled
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.AudioOutput),
                text = MR.strings.audioOutput,
                isChecked = viewModel.isAudioOutputEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleAudioOutputEnabled
            )

            //intent handling enabled
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.IntentHandling),
                text = MR.strings.intentHandling,
                isChecked = viewModel.isIntentHandlingEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleIntentHandlingEnabled
            )

        }

    }

}