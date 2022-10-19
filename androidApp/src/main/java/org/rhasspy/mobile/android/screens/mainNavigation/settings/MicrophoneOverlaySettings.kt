package org.rhasspy.mobile.android.screens.mainNavigation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.settings.MicrophoneOverlaySettingsViewModel

@Preview
@Composable
fun MicrophoneOverlaySettingsContent(viewModel: MicrophoneOverlaySettingsViewModel = viewModel()) {

    PageContent(MR.strings.microphoneOverlay) {

        Column {

            SwitchListItem(
                text = MR.strings.showMicrophoneOverlay,
                secondaryText = MR.strings.showMicrophoneOverlayInfo,
                isChecked = viewModel.isMicrophoneOverlayEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleMicrophoneOverlayEnabled
            )

            SwitchListItem(
                text = MR.strings.whileAppIsOpened,
                isChecked = viewModel.isMicrophoneOverlayWhileAppEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleMicrophoneOverlayWhileAppEnabled
            )

        }

    }

}