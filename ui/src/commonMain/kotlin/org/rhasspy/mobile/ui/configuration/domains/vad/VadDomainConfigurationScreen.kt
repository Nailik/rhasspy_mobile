package org.rhasspy.mobile.ui.configuration.domains.vad

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
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.VadDomainOption
import org.rhasspy.mobile.platformspecific.roundToDecimals
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.InformationListElement
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SliderListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.viewmodel.configuration.vad.AudioRecorderViewState
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.Change.SelectVadDomainOption
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.LocalSilenceDetectionUiEvent
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.LocalSilenceDetectionUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.LocalSilenceDetectionUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState.VadDomainConfigurationData.LocalSilenceDetectionConfigurationData

@Composable
fun VoiceActivityDetectionConfigurationScreen(viewModel: VadDomainConfigurationViewModel) {


    ScreenContent(
        title = MR.strings.voice_activity_detection.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
    ) {

        val viewState by viewModel.viewState.collectAsState()
        val audioRecorderViewState by viewModel.audioRecorderViewState.collectAsState()

        VoiceActivityDetectionOptionContent(
            viewState = viewState,
            audioRecorderViewState = audioRecorderViewState,
            onEvent = viewModel::onEvent,
        )

    }

}

@Composable
private fun VoiceActivityDetectionOptionContent(
    viewState: VadDomainViewState,
    audioRecorderViewState: AudioRecorderViewState,
    onEvent: (VadDomainUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.WakeWordOptions),
        selected = viewState.editData.vadDomainOption,
        onSelect = { onEvent(SelectVadDomainOption(it)) },
        values = viewState.editData.vadDomainOptions
    ) { option ->

        when (option) {
            VadDomainOption.Local ->
                SilenceDetectionSettingsContent(
                    editData = viewState.editData.localSilenceDetectionSetting,
                    audioRecorderViewState = audioRecorderViewState,
                    onEvent = onEvent
                )

            VadDomainOption.Disabled -> Unit
        }

    }

}


/**
 * settings to configure automatic silence detection
 */

@Composable
private fun SilenceDetectionSettingsContent(
    editData: LocalSilenceDetectionConfigurationData,
    audioRecorderViewState: AudioRecorderViewState,
    onEvent: (LocalSilenceDetectionUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .testTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)
            .padding(bottom = 16.dp)
    ) {

        InformationListElement(text = MR.strings.silenceDetectionInformation.stable)

        Time(
            silenceDetectionMinimumTime = editData.silenceDetectionMinimumTime,
            silenceDetectionTime = editData.silenceDetectionTime,
            onEvent = onEvent,
        )

        CurrentAudioLevel(
            isRecording = audioRecorderViewState.isRecording,
            isAudioLevelBiggerThanMax = audioRecorderViewState.isAudioLevelBiggerThanMax,
            audioLevelPercentage = audioRecorderViewState.audioLevelPercentage,
            currentVolume = audioRecorderViewState.currentVolume,
        )

        AudioLevel(
            silenceDetectionAudioLevelPercentage = audioRecorderViewState.silenceDetectionAudioLevelPercentage,
            silenceDetectionAudioLevel = editData.silenceDetectionAudioLevel,
            onEvent = onEvent,
        )

        StartTestButton(
            isRecording = audioRecorderViewState.isRecording,
            onEvent = onEvent,
        )

    }

}

/**
 * time duration of silence detection
 */
@Composable
private fun Time(
    silenceDetectionMinimumTime: String,
    silenceDetectionTime: String,
    onEvent: (LocalSilenceDetectionUiEvent) -> Unit
) {

    TextFieldListItem(
        label = MR.strings.silenceDetectionMinimumTime.stable,
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsMinimumTime),
        value = silenceDetectionMinimumTime,
        onValueChange = { onEvent(UpdateSilenceDetectionMinimumTime(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isLastItem = false
    )

    TextFieldListItem(
        label = MR.strings.silenceDetectionTime.stable,
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsTime),
        value = silenceDetectionTime,
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
    onEvent: (LocalSilenceDetectionUiEvent) -> Unit
) {

    SliderListItem(
        text = MR.strings.audioLevelThreshold.stable,
        value = silenceDetectionAudioLevelPercentage,
        valueText = silenceDetectionAudioLevel.roundToDecimals(0).toString(),
        onValueChange = { onEvent(UpdateSilenceDetectionAudioLevelLogarithm(it)) }
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
    onEvent: (LocalSilenceDetectionUiEvent) -> Unit
) {

    ListElement {

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .testTag(TestTag.AutomaticSilenceDetectionSettingsTest)
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
                onClick = { onEvent(ToggleAudioLevelTest) }
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