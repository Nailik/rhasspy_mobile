package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
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
import org.rhasspy.mobile.viewModels.settings.MicrophoneOverlaySettingsViewModel

/**
 * Settings vor Microphone overlay
 *
 * overlay on/off
 * overlay while app is opened on/off
 */
@Preview
@Composable
fun MicrophoneOverlaySettingsContent(viewModel: MicrophoneOverlaySettingsViewModel = viewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.MicrophoneOverlaySettings),
        title = MR.strings.microphoneOverlay) {

        Column {

            //overlay permission request
            RequiresOverlayPermission({ viewModel.toggleMicrophoneOverlayEnabled(true) }) { onClick ->

                //overlay switch on/off
                SwitchListItem(
                    text = MR.strings.showMicrophoneOverlay,
                    secondaryText = MR.strings.showMicrophoneOverlayInfo,
                    isChecked = viewModel.isMicrophoneOverlayEnabled.collectAsState().value,
                    onCheckedChange = { if (it) onClick.invoke() else viewModel.toggleMicrophoneOverlayEnabled(false) }
                )

            }

            //visibility of overlay while app
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewModel.isMicrophoneOverlayWhileAppEnabledVisible.collectAsState().value
            ) {

                SwitchListItem(
                    text = MR.strings.whileAppIsOpened,
                    isChecked = viewModel.isMicrophoneOverlayWhileAppEnabled.collectAsState().value,
                    onCheckedChange = viewModel::toggleMicrophoneOverlayWhileAppEnabled
                )

            }

        }

    }

}