package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.RequiresOverlayPermission
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.settings.MicrophoneOverlaySettingsViewModel

@Preview
@Composable
fun MicrophoneOverlaySettingsContent(viewModel: MicrophoneOverlaySettingsViewModel = viewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.MicrophoneOverlaySettings),
        title = MR.strings.microphoneOverlay) {

        Column {

            RequiresOverlayPermission({ viewModel.toggleMicrophoneOverlayEnabled(true) }) { onClick ->
                SwitchListItem(
                    text = MR.strings.showMicrophoneOverlay,
                    secondaryText = MR.strings.showMicrophoneOverlayInfo,
                    isChecked = viewModel.isMicrophoneOverlayEnabled.collectAsState().value,
                    onCheckedChange = { if (it) onClick.invoke() else viewModel.toggleMicrophoneOverlayEnabled(false) }
                )
            }

            SwitchListItem(
                text = MR.strings.whileAppIsOpened,
                isChecked = viewModel.isMicrophoneOverlayWhileAppEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleMicrophoneOverlayWhileAppEnabled
            )

            Spacer(modifier = Modifier.fillMaxWidth(1f))

            Button(onClick = viewModel::save) {
                Text(MR.strings.save)
            }

        }

    }

}