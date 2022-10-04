package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun MicrophoneOverlayItem(viewModel: SettingsScreenViewModel) {

    val isMicrophoneOverlayEnabled by viewModel.isMicrophoneOverlayEnabled.collectAsState()

    ExpandableListItem(
        text = MR.strings.microphoneOverlay,
        secondaryText = isMicrophoneOverlayEnabled.toText()
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.showMicrophoneOverlay,
                secondaryText = MR.strings.showMicrophoneOverlayInfo,
                isChecked = isMicrophoneOverlayEnabled,
                onCheckedChange = viewModel::updateMicrophoneEnabled
            )

            SwitchListItem(
                text = MR.strings.whileAppIsOpened,
                isChecked = viewModel.isMicrophoneOverlayWhileApp.collectAsState().value,
                onCheckedChange = viewModel::updateMicrophoneOverlayWhileApp
            )
        }
    }
}