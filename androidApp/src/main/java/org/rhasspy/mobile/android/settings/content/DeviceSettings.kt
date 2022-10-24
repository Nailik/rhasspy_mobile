package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.PageContent
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
fun DeviceSettingsContent(viewModel: DeviceSettingsSettingsViewModel = viewModel()) {

    PageContent(MR.strings.device,) {

        Column {

            //volume slider
            SliderListItem(
                text = MR.strings.volume,
                value = viewModel.volume.collectAsState().value,
                onValueChange = viewModel::updateVolume)

            //hot word enabled
            SwitchListItem(
                text = MR.strings.hotWord,
                isChecked = viewModel.isHotWordEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHotWordEnabled
            )

            //audio output enabled
            SwitchListItem(
                text = MR.strings.audioOutput,
                isChecked = viewModel.isAudioOutputEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleAudioOutputEnabled
            )

            //intent handling enabled
            SwitchListItem(
                text = MR.strings.intentHandling,
                isChecked = viewModel.isIntentHandlingEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleIntentHandlingEnabled
            )

        }

    }

}