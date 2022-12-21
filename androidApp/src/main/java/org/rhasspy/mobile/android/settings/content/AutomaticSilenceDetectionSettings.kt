package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.OnPauseEffect
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.settings.AutomaticSilenceDetectionSettingsViewModel

/**
 * settings to configure automatic silence detection
 */
@Preview
@Composable
fun AutomaticSilenceDetectionSettingsContent(viewModel: AutomaticSilenceDetectionSettingsViewModel = get()) {

    OnPauseEffect(viewModel::onPause)

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.AutomaticSilenceDetectionSettings),
        title = MR.strings.automaticSilenceDetection
    ) {

        //toggle
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.EnabledSwitch),
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

            Column(modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)) {

                Time(viewModel)

                CurrentAudioLevel(viewModel)

                AudioLevel(viewModel)

                StartTestButton(viewModel)

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
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsTime),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.automaticSilenceDetectionTimeText.collectAsState().value,
        onValueChange = viewModel::updateAutomaticSilenceDetectionTime
    )

}

/**
 * audio level of silence detection
 */
@Composable
private fun AudioLevel(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    SliderListItem(
        text = MR.strings.audioLevelThreshold,
        value = viewModel.automaticSilenceDetectionAudioLevelPercentage.collectAsState().value,
        valueText = "%.0f".format(
            null,
            viewModel.automaticSilenceDetectionAudioLevel.collectAsState().value
        ),
        onValueChange = viewModel::changeAutomaticSilenceDetectionAudioLevelPercentage
    )

}


/**
 * testing of audio level
 */
@Composable
private fun CurrentAudioLevel(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isSilenceDetectionAudioLevelVisible.collectAsState().value
    ) {
        ListElement(modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)) {
            val animatedColor by animateColorAsState(
                targetValue = if (viewModel.isAudioLevelBiggerThanMax.collectAsState().value) {
                    MaterialTheme.colorScheme.errorContainer
                } else MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50))
            ) {
                val animatedSize by animateFloatAsState(targetValue = viewModel.audioLevelPercentage.collectAsState().value)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedSize)
                        .background(animatedColor)
                )
                Text(
                    text = viewModel.currentAudioLevel.collectAsState().value.toString(),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.Transparent)
                )
            }
        }
    }

}

/**
 * button to start automatic silence detection test
 */
@Composable
private fun StartTestButton(viewModel: AutomaticSilenceDetectionSettingsViewModel) {

    RequiresMicrophonePermission(
        MR.strings.microphonePermissionInfoRecord,
        viewModel::toggleAudioLevelTest
    ) { onClick ->
        ListElement {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .testTag(TestTag.AutomaticSilenceDetectionSettingsTest)
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally),
                    onClick = onClick
                ) {

                    val isRecording by viewModel.isRecording.collectAsState()

                    Icon(
                        if (isRecording) Icons.Filled.MicOff else Icons.Filled.Mic,
                        MR.strings.microphone
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(if (isRecording) MR.strings.stop else MR.strings.testAudioLevel)

                }

            }
        }
    }

}