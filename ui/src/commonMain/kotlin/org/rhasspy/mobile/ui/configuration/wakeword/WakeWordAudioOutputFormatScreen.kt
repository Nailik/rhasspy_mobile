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
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.AudioOutputFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioOutputConfigurationData

@Composable
fun WakeWordAudioOutputFormatScreen() {
    val viewModel: WakeWordConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = MR.strings.wakeWordAudioOutputFormat.stable,
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
                    viewState = editData.wakeWordAudioOutputData,
                    onEvent = viewModel::onEvent
                )

            }
        }
    }
}

@Composable
private fun WakeWordAudioOutputFormatScreenContent(
    isEncodingChangeEnabled: Boolean,
    viewState: WakeWordAudioOutputConfigurationData,
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}
