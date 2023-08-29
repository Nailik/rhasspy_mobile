package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioOutputConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioRecorderConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioOutputConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioRecorderConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

@Composable
fun AudioInputConfigurationScreen() {

    val viewModel: AudioInputConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        AudioInputConfigurationScreenContent(
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioInputConfigurationScreenContent(
    onEvent: (AudioInputConfigurationUiEvent) -> Unit,
    viewState: AudioInputConfigurationViewState
) {

    Scaffold(
        modifier = Modifier
            .testTag(NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.audio_input.stable) },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(BackClick) },
                        modifier = Modifier.testTag(TestTag.AppBarBackButton)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = MR.strings.back.stable,
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .testTag(TestTag.List)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AudioInputWakeWord(
                onEvent = onEvent,
                wakeWordAudioRecorderData = viewState.wakeWordAudioRecorderData,
                wakeWordAudioOutputData = viewState.wakeWordAudioOutputData
            )

            AudioInputSpeechToText(
                onEvent = onEvent,
                speechToTextAudioRecorderData = viewState.speechToTextAudioRecorderData,
                speechToTextAudioOutputData = viewState.speechToTextAudioOutputData
            )
        }
    }
}

@Composable
private fun AudioInputWakeWord(
    onEvent: (AudioInputConfigurationUiEvent) -> Unit,
    wakeWordAudioRecorderData: WakeWordAudioRecorderConfigurationData,
    wakeWordAudioOutputData: WakeWordAudioOutputConfigurationData,
) {

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        ListElement {
            Text(resource = MR.strings.wakeWord.stable)
        }

        Divider()

        //button to open audio recorder format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenWakeWordRecorderFormatScreen) },
            text = { Text(MR.strings.audioRecorderFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(wakeWordAudioRecorderData.audioRecorderChannelType.text)} | " +
                        "${translate(wakeWordAudioRecorderData.audioRecorderEncodingType.text)} | " +
                        translate(wakeWordAudioRecorderData.audioRecorderSampleRateType.text)
                Text(text = text)
            }
        )

        Divider()

        //button to open audio output format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenWakeWordOutputFormatScreen) },
            text = { Text(MR.strings.audioOutputFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(wakeWordAudioOutputData.audioOutputChannelType.text)} | " +
                        "${translate(wakeWordAudioOutputData.audioOutputEncodingType.text)} | " +
                        translate(wakeWordAudioOutputData.audioOutputSampleRateType.text)
                Text(text = text)
            }
        )

    }

}

@Composable
private fun AudioInputSpeechToText(
    onEvent: (AudioInputConfigurationUiEvent) -> Unit,
    speechToTextAudioRecorderData: SpeechToTextAudioRecorderConfigurationData,
    speechToTextAudioOutputData: SpeechToTextAudioOutputConfigurationData,
) {

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        ListElement {
            Text(resource = MR.strings.speechToText.stable)
        }

        Divider()

        //button to open audio recorder format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenTextToSpeechRecorderFormatScreen) },
            text = { Text(resource = MR.strings.audioRecorderFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioRecorderData.audioRecorderChannelType.text)} | " +
                        "${translate(speechToTextAudioRecorderData.audioRecorderEncodingType.text)} | " +
                        translate(speechToTextAudioRecorderData.audioRecorderSampleRateType.text)
                Text(text = text)
            }
        )

        Divider()

        //button to open audio output format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenTextToSpeechOutputFormatScreen) },
            text = { Text(MR.strings.audioOutputFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioOutputData.audioOutputChannelType.text)} | " +
                        "${translate(speechToTextAudioOutputData.audioOutputEncodingType.text)} | " +
                        translate(speechToTextAudioOutputData.audioOutputSampleRateType.text)
                Text(text = text)
            }
        )

    }

}