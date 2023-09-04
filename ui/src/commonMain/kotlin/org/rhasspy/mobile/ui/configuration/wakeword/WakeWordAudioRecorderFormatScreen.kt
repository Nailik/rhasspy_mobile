package org.rhasspy.mobile.ui.configuration.wakeword

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
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioRecorderConfigurationData

@Composable
fun WakeWordAudioRecorderFormatScreen(viewModel: WakeWordConfigurationViewModel) {

    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData

    ScreenContent(
        screenViewModel = viewModel
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppBar(
                    title = MR.strings.wakeWordAudioRecorderFormat.stable,
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

                    WakeWordAudioRecorderFormatScreenContent(
                        isEncodingChangeEnabled = viewState.isRecorderEncodingChangeEnabled,
                        viewState = editData.wakeWordAudioRecorderData,
                        onEvent = viewModel::onEvent
                    )

                }
            }
        }

    }

}

@Composable
private fun WakeWordAudioRecorderFormatScreenContent(
    isEncodingChangeEnabled: Boolean,
    viewState: WakeWordAudioRecorderConfigurationData,
    onEvent: (event: WakeWordConfigurationUiEvent) -> Unit
) {
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
            onSelect = { onEvent(SelectAudioRecorderChannelType(it)) },
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
            enabled = isEncodingChangeEnabled,
            onSelect = { onEvent(SelectAudioRecorderEncodingType(it)) },
            combinedTestTag = TestTag.AudioRecorderEncodingType,
            values = viewState.audioRecorderEncodingTypes
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
            modifier = Modifier.testTag(TestTag.AudioRecorderSampleRateType),
            selected = viewState.audioRecorderSampleRateType,
            onSelect = { onEvent(SelectAudioRecorderSampleRateType(it)) },
            combinedTestTag = TestTag.AudioRecorderSampleRateType,
            values = viewState.audioRecorderSampleRateTypes
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}
