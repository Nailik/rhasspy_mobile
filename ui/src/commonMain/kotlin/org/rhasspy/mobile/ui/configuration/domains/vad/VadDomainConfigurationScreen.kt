package org.rhasspy.mobile.ui.configuration.domains.vad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
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
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.AudioRecorderViewState
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.Change.SelectVoiceActivityDetectionOption
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionViewState
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionViewState.VoiceActivityDetectionConfigurationData.LocalSilenceDetectionConfigurationData

@Composable
fun VoiceActivityDetectionConfigurationScreen(viewModel: VoiceActivityDetectionConfigurationViewModel) {

    ScreenContent(
        screenViewModel = viewModel
    ) {
        SettingsScreenItemContent(
            title = MR.strings.textToSpeech.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            val viewState by viewModel.viewState.collectAsState()
            val audioRecorderViewState by viewModel.audioRecorderViewState.collectAsState()

            VoiceActivityDetectionEditContent(
                viewState = viewState,
                audioRecorderViewState = audioRecorderViewState,
                onEvent = viewModel::onEvent
            )

        }
    }

}


@Composable
private fun VoiceActivityDetectionEditContent(
    viewState: VoiceActivityDetectionViewState,
    audioRecorderViewState: AudioRecorderViewState,
    onEvent: (VoiceActivityDetectionUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            VoiceActivityDetectionOptionContent(
                viewState = viewState,
                audioRecorderViewState = audioRecorderViewState,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun VoiceActivityDetectionOptionContent(
    viewState: VoiceActivityDetectionViewState,
    audioRecorderViewState: AudioRecorderViewState,
    onEvent: (VoiceActivityDetectionUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.WakeWordOptions),
        selected = viewState.editData.voiceActivityDetectionOption,
        onSelect = { onEvent(SelectVoiceActivityDetectionOption(it)) },
        values = viewState.editData.voiceActivityDetectionOptions
    ) { option ->

        when (option) {
            VoiceActivityDetectionOption.Local ->
                SilenceDetectionSettingsContent(
                    editData = viewState.editData.localSilenceDetectionSetting,
                    audioRecorderViewState = audioRecorderViewState,
                    onEvent = onEvent
                )

            VoiceActivityDetectionOption.Disabled -> Unit
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
            silenceDetectionMinimumTimeText = editData.silenceDetectionMinimumTimeText,
            silenceDetectionTimeText = editData.silenceDetectionTimeText,
            onEvent = onEvent
        )

        CurrentAudioLevel(
            isRecording = audioRecorderViewState.isRecording,
            isAudioLevelBiggerThanMax = audioRecorderViewState.isAudioLevelBiggerThanMax,
            audioLevelPercentage = audioRecorderViewState.audioLevelPercentage,
            currentVolume = audioRecorderViewState.currentVolume
        )

        AudioLevel(
            silenceDetectionAudioLevelPercentage = audioRecorderViewState.silenceDetectionAudioLevelPercentage,
            silenceDetectionAudioLevel = editData.silenceDetectionAudioLevel,
            onEvent = onEvent
        )

        StartTestButton(
            isRecording = audioRecorderViewState.isRecording,
            onEvent = onEvent
        )

    }

}

/**
 * time duration of silence detection
 */
@Composable
private fun Time(
    silenceDetectionMinimumTimeText: String,
    silenceDetectionTimeText: String,
    onEvent: (LocalSilenceDetectionUiEvent) -> Unit
) {

    TextFieldListItem(
        label = MR.strings.silenceDetectionMinimumTime.stable,
        modifier = Modifier.testTag(TestTag.AutomaticSilenceDetectionSettingsMinimumTime),
        value = silenceDetectionMinimumTimeText,
        onValueChange = { onEvent(UpdateSilenceDetectionMinimumTime(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isLastItem = false
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