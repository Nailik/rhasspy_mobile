package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.settings.AutomaticSilenceDetectionSettingsViewModel


@Preview
@Composable
fun AutomaticSilenceDetectionSettingsContent(viewModel: AutomaticSilenceDetectionSettingsViewModel = viewModel()) {

    PageContent(MR.strings.automaticSilenceDetection) {

        //toggle
        SwitchListItem(
            text = MR.strings.automaticSilenceDetection,
            isChecked = viewModel.isAutomaticSilenceDetectionEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleAutomaticSilenceDetectionEnabled
        )

        //silence settings visible
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isSilenceDetectionSettingsVisible.collectAsState().value
        ) {

            Column {

                Time(viewModel)

                AudioLevel(viewModel)

                Test(viewModel)

            }

        }

    }
}

/**
 * time duration of silence detection
 */
@Composable
private fun Time(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    TextFieldListItem(
        label = MR.strings.silenceDetectionTime,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.automaticSilenceDetectionTime.collectAsState().value.toString(),
        onValueChange = viewModel::updateAutomaticSilenceDetectionTime
    )

}

/**
 * audio level of silence detection
 */
@Composable
private fun AudioLevel(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    TextFieldListItem(
        label = MR.strings.audioLevelThreshold,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.automaticSilenceDetectionAudioLevel.collectAsState().value.toString(),
        onValueChange = viewModel::updateAutomaticSilenceDetectionAudioLevel
    )

}

/**
 * testing of audio level
 */
@Composable
private fun Test(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        val audioLevel by viewModel.currentAudioLevel.collectAsState()

        val animatedWeight = animateFloatAsState(targetValue = if (viewModel.currentStatus.collectAsState().value) 1f else 0f)
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

/**
 * button to start automatic silence detection test
 */
@Composable
private fun RowScope.StartTestButton(viewModel: AutomaticSilenceDetectionSettingsViewModel, animatedWeight: Float) {

    Button(
        modifier = Modifier
            .weight(2f - animatedWeight)
            .wrapContentSize(),
        onClick = viewModel::toggleAudioLevelTest
    )
    {

        val status by viewModel.currentStatus.collectAsState()

        Icon(if (status) Icons.Filled.MicOff else Icons.Filled.Mic, MR.strings.microphone)

        Spacer(modifier = Modifier.width(8.dp))

        Text(if (status) MR.strings.stop else MR.strings.testAudioLevel)

    }

}