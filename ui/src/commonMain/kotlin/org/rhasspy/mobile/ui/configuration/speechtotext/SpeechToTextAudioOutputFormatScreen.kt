package org.rhasspy.mobile.ui.configuration.speechtotext

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputChannelType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputEncodingType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputSampleRateType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioOutputFormatConfigurationData

@Composable
fun SpeechToTextAudioOutputFormatScreen() {
    val viewModel: SpeechToTextConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = MR.strings.speechToTextAudioOutputFormat.stable,
                onBackClick = { viewModel.onEvent(BackClick) }
            )
        },
    ) { paddingValues ->
        Surface(tonalElevation = 1.dp) {

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                WakeWordAudioOutputFormatScreenContent(
                    isEncodingChangeEnabled = viewState.isOutputEncodingChangeEnabled,
                    viewState = editData.speechToTextAudioOutputFormatData,
                    onEvent = viewModel::onEvent
                )

            }
        }
    }
}

@Composable
private fun WakeWordAudioOutputFormatScreenContent(
    isEncodingChangeEnabled: Boolean,
    viewState: SpeechToTextAudioOutputFormatConfigurationData,
    onEvent: (event: SpeechToTextConfigurationUiEvent) -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        ListElement {
            Text(resource = MR.strings.channel.stable)
        }

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputChannelType),
            selected = viewState.audioOutputChannelType,
            onSelect = { onEvent(SelectAudioOutputChannelType(it)) },
            combinedTestTag = TestTag.AudioOutputChannelType,
            values = viewState.audioOutputChannelTypes
        )
    }

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        ListElement {
            Text(resource = MR.strings.encoding.stable)
        }

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputEncodingType),
            selected = viewState.audioOutputEncodingType,
            enabled = isEncodingChangeEnabled,
            onSelect = { onEvent(SelectAudioOutputEncodingType(it)) },
            combinedTestTag = TestTag.AudioOutputEncodingType,
            values = viewState.audioOutputEncodingTypes
        )
    }

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        ListElement {
            Text(resource = MR.strings.sampleRate.stable)
        }

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputSampleRateType),
            selected = viewState.audioOutputSampleRateType,
            onSelect = { onEvent(SelectAudioOutputSampleRateType(it)) },
            combinedTestTag = TestTag.AudioOutputSampleRateType,
            values = viewState.audioOutputSampleRateTypes
        )
    }
}

/**
 * top app bar with title and back navigation button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: StableStringResource, onBackClick: () -> Unit) {

    TopAppBar(
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                org.rhasspy.mobile.ui.content.elements.Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}
