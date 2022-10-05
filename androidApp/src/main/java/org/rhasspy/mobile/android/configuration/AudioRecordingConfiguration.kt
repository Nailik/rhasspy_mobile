package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.ConfigurationScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for audio recording configuration
 * shows if udp audio output is turned on
 */
@Composable
fun AudioRecordingConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.audioRecording,
        secondaryText = if (viewModel.isUdpOutputEnabled.flow.collectAsState().value) {
            MR.strings.udpAudioOutputOn
        } else {
            MR.strings.udpAudioOutputOff
        },
        screen = ConfigurationScreens.AudioRecording
    )

}

/**
 * Content to configure audio recording
 * Udp audio output on/off
 * also udp host and port
 */
@Composable
fun AudioRecordingConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.audioRecording) {

        SwitchListItem(
            text = MR.strings.udpAudioOutput,
            secondaryText = MR.strings.udpAudioOutputDetail,
            isChecked = viewModel.isUdpOutputEnabled.flow.collectAsState().value,
            onCheckedChange = viewModel.isUdpOutputEnabled::set
        )

        UdpSettings(viewModel)

    }
}

/**
 *  udp host and port
 */
@Composable
private fun UdpSettings(viewModel: ConfigurationScreenViewModel) {

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isUdpOutputEnabled.flow.collectAsState().value
    ) {

        Column {

            TextFieldListItem(
                label = MR.strings.host,
                value = viewModel.udpOutputHost.flow.collectAsState().value,
                onValueChange = viewModel.udpOutputHost::set
            )

            TextFieldListItem(
                label = MR.strings.port,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = viewModel.udpOutputPort.flow.collectAsState().value,
                onValueChange = viewModel.udpOutputPort::set
            )
        }
    }
}