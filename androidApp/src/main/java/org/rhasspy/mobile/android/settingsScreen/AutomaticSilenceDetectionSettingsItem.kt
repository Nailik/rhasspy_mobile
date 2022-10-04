package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.navigation.LocalSnackbarHostState
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel


@Composable
fun AutomaticSilenceDetectionItem(viewModel: SettingsScreenViewModel) {

    val isAutomaticSilenceDetectionEnabled by viewModel.isAutomaticSilenceDetectionEnabled.collectAsState()

    ExpandableListItem(
        text = MR.strings.automaticSilenceDetection,
        secondaryText = isAutomaticSilenceDetectionEnabled.toText()
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.automaticSilenceDetection,
                isChecked = isAutomaticSilenceDetectionEnabled,
                onCheckedChange = viewModel::updateAutomaticSilenceDetectionEnabled
            )

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isAutomaticSilenceDetectionEnabled
            ) {

                Column {

                    Time(viewModel)

                    AudioLevel(viewModel)

                    Test(viewModel)

                }
            }
        }
    }
}

@Composable
private fun Time(viewModel: SettingsScreenViewModel) {
    TextFieldListItem(
        label = MR.strings.silenceDetectionTime,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.automaticSilenceDetectionTime.collectAsState().value.toString(),
        onValueChange = viewModel::updateAutomaticSilenceDetectionTime
    )
}

@Composable
private fun AudioLevel(viewModel: SettingsScreenViewModel) {
    TextFieldListItem(
        label = MR.strings.audioLevelThreshold,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.automaticSilenceDetectionAudioLevel.collectAsState().value.toString(),
        onValueChange = viewModel::updateAutomaticSilenceDetectionAudioLevel
    )
}


@Composable
private fun Test(viewModel: SettingsScreenViewModel) {

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        val status = viewModel.status.collectAsState().value
        val audioLevel = viewModel.audioLevel.collectAsState().value

        val animatedWeight = animateFloatAsState(targetValue = if (status) 1f else 0f)
        val animatedColor = animateColorAsState(
            targetValue = if (audioLevel > viewModel.automaticSilenceDetectionAudioLevel.collectAsState().value) {
                MaterialTheme.colorScheme.error
            } else MaterialTheme.colorScheme.primary
        )

        if (animatedWeight.value > 0f) {

            OutlinedButton(
                modifier = Modifier
                    .weight(animatedWeight.value)
                    .alpha(animatedWeight.value)
                    .padding(end = 16.dp)
                    .fillMaxWidth(),
                border = BorderStroke(ButtonDefaults.outlinedButtonBorder.width, animatedColor.value),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = animatedColor.value),
                onClick = { })
            {
                Text(audioLevel.toString())
            }
        }

        StartTestButton(viewModel, animatedWeight.value)
    }
}

@Composable
private fun RowScope.StartTestButton(viewModel: SettingsScreenViewModel, animatedWeight: Float) {
    val status = viewModel.status.collectAsState().value

    val requestMicrophonePermission =
        requestMicrophonePermission(LocalSnackbarHostState.current, MR.strings.microphonePermissionInfoAudioLevel) {
            if (it) {
                //TODO use view model to request persmission if necessary
                viewModel.toggleAudioLevelTest()
            }
        }

    Button(
        modifier = Modifier
            .weight(2f - animatedWeight)
            .wrapContentSize(),
        onClick = { requestMicrophonePermission.invoke() })
    {
        Icon(if (status) Icons.Filled.MicOff else Icons.Filled.Mic, MR.strings.microphone)

        Spacer(modifier = Modifier.width(8.dp))

        Text(if (status) MR.strings.stop else MR.strings.testAudioLevel)
    }
}