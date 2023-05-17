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
import org.rhasspy.mobile.android.content.list.*
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.SilenceDetectionSettings
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel

/**
 * settings to configure automatic silence detection
 */
@Preview
@Composable
fun SilenceDetectionSettingsContent() {
    val viewModel: SilenceDetectionSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        SettingsScreenItemContent(
            modifier = Modifier.testTag(SilenceDetectionSettings),
            title = MR.strings.automaticSilenceDetection.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            //toggle
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.EnabledSwitch),
                text = MR.strings.automaticSilenceDetection.stable,
                isChecked = viewState.isSilenceDetectionEnabled,
                onCheckedChange = { viewModel.onEvent(SetSilenceDetectionEnabled(it)) }
            )

            //silence settings visible
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewState.isSilenceDetectionEnabled
            ) {

                Column(modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)) {

                    InformationListElement(text = MR.strings.silenceDetectionInformation.stable)

                    Time(
                        silenceDetectionMinimumTimeText = viewState.silenceDetectionMinimumTimeText,
                        silenceDetectionTimeText = viewState.silenceDetectionTimeText,
                        onEvent = viewModel::onEvent
                    )

                    CurrentAudioLevel(
                        isRecording = viewState.isRecording,
                        isAudioLevelBiggerThanMax = viewState.isAudioLevelBiggerThanMax,
                        audioLevelPercentage = viewState.audioLevelPercentage,
                        currentVolume = viewState.currentVolume
                    )

                    AudioLevel(
                        silenceDetectionAudioLevelPercentage = viewState.silenceDetectionAudioLevelPercentage,
                        silenceDetectionAudioLevel = viewState.silenceDetectionAudioLevel,
                        onEvent = viewModel::onEvent
                    )

                    StartTestButton(
                        isRecording = viewState.isRecording,
                        onEvent = viewModel::onEvent
                    )

                }

            }

        }
    }
}

/**
 * time duration of silence detection
 */
@Composable
private fun Time(
    silenceDetectionMinimumTimeText: String,
    silenceDetectionTimeText: String,
    onEvent: (SilenceDetectionSettingsUiEvent) -> Unit
) {

    TextFieldListItem(
        label = MR.strings.silenceDetectionMinimumTime.stable,
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsMinimumTime),
        value = silenceDetectionMinimumTimeText,
        onValueChange = { onEvent(UpdateSilenceDetectionMinimumTime(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

    TextFieldListItem(
        label = MR.strings.silenceDetectionTime.stable,
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsTime),
        value = silenceDetectionTimeText,
        onValueChange = { onEvent(UpdateSilenceDetectionTime(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

}

/**
 * audio level of silence detection
 */
@Composable
private fun AudioLevel(
    silenceDetectionAudioLevelPercentage: Float,
    silenceDetectionAudioLevel: Float,
    onEvent: (SilenceDetectionSettingsUiEvent) -> Unit
) {

    SliderListItem(
        text = MR.strings.audioLevelThreshold.stable,
        value = silenceDetectionAudioLevelPercentage,
        valueText = "%.0f".format(null, silenceDetectionAudioLevel),
        onValueChange = { onEvent(UpdateSilenceDetectionAudioLevelPercentage(it)) }
    )

}


/**
 * testing of audio level
 */
@Composable
private fun CurrentAudioLevel(
    isRecording: Boolean,
    isAudioLevelBiggerThanMax: Boolean,
    audioLevelPercentage: Float,
    currentVolume: String
) {

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isRecording
    ) {
        ListElement(modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)) {
            val animatedColor by animateColorAsState(
                targetValue = if (isAudioLevelBiggerThanMax) {
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
                val animatedSize by animateFloatAsState(targetValue = audioLevelPercentage)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedSize)
                        .background(animatedColor)
                )
                Text(
                    text = currentVolume,
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
private fun StartTestButton(
    isRecording: Boolean,
    onEvent: (SilenceDetectionSettingsUiEvent) -> Unit
) {

    RequiresMicrophonePermission(
        informationText = MR.strings.microphonePermissionInfoRecord.stable,
        onClick = { onEvent(ToggleAudioLevelTest) }
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

                    Icon(
                        if (isRecording) Icons.Filled.MicOff else Icons.Filled.Mic,
                        MR.strings.microphone.stable
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(if (isRecording) MR.strings.stop.stable else MR.strings.testAudioLevel.stable)

                }

            }
        }
    }

}