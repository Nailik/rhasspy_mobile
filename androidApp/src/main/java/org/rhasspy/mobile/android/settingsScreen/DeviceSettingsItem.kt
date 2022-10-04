package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun DeviceSettingsItem(viewModel: SettingsScreenViewModel) {

    ExpandableListItem(
        text = MR.strings.device,
        secondaryText = MR.strings.deviceSettingsInformation
    ) {
        Column {
            SliderListItem(
                text = MR.strings.volume,
                value = viewModel.volume.collectAsState().value,
                onValueChange = viewModel::updateVolume)

            SwitchListItem(
                text = MR.strings.hotWord,
                isChecked = viewModel.isHotWordEnabled.collectAsState().value,
                onCheckedChange = viewModel::updateHotWordEnabled)

            SwitchListItem(
                text = MR.strings.audioOutput,
                isChecked = viewModel.isAudioOutputEnabled.collectAsState().value,
                onCheckedChange = viewModel::updateAudioOutputEnabled)

            SwitchListItem(
                text = MR.strings.intentHandling,
                isChecked = viewModel.isIntentHandlingEnabled.collectAsState().value,
                onCheckedChange = viewModel::updateIntentHandlingEnabled)
        }
    }
}