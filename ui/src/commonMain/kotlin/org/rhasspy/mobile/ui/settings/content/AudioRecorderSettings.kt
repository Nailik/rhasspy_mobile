package org.rhasspy.mobile.ui.settings.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.main.LocalViewModelFactory
import org.rhasspy.mobile.ui.settings.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Navigate.BackClick
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsViewModel

@Composable
fun AudioRecorderSettingsContent() {
    val viewModel: AudioRecorderSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier,
        title = MR.strings.automaticSilenceDetection.stable,
                onBackClick = { viewModel.onEvent(BackClick) }
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            ListElement {
                Text(resource = MR.strings.sampleRate.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioRecorderSampleRateType),
                selected = viewState.audioRecorderSampleRateType,
                onSelect = { viewModel.onEvent(SelectAudioRecorderSampleRateType(it)) },
                combinedTestTag = TestTag.AudioRecorderSampleRateType,
                values = viewState.audioRecorderSampleRateTypes
            )
        }

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            ListElement {
                Text(resource = MR.strings.channel.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioRecorderChannelType),
                selected = viewState.audioRecorderChannelType,
                onSelect = { viewModel.onEvent(SelectAudioRecorderChannelType(it)) },
                combinedTestTag = TestTag.AudioRecorderChannelType,
                values = viewState.audioRecorderChannelTypes
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
                modifier = Modifier.testTag(TestTag.AudioRecorderEncodingType),
                selected = viewState.audioRecorderEncodingType,
                onSelect = { viewModel.onEvent(SelectAudioRecorderEncodingType(it)) },
                combinedTestTag = TestTag.AudioRecorderEncodingType,
                values = viewState.audioRecorderEncodingTypes
            )
        }


    }

}