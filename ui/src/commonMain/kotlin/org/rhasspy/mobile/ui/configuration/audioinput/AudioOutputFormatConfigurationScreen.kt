package org.rhasspy.mobile.ui.configuration.audioinput

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewState.AudioOutputFormatConfigurationData

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioOutputFormatConfigurationScreen(viewModel: AudioOutputFormatConfigurationViewModel) {

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.speechToText.stable, //TODO
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        AudioOutputFormatEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun AudioOutputFormatEditContent(
    editData: AudioOutputFormatConfigurationData,
    onEvent: (AudioOutputFormatConfigurationUiEvent) -> Unit
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
            selected = editData.audioOutputChannel,
            onSelect = { onEvent(SelectOutputFormatChannelType(it)) },
            combinedTestTag = TestTag.AudioOutputChannelType,
            values = editData.audioFormatChannelTypes
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
            selected = editData.audioOutputEncoding,
            enabled = true, //TODO
            onSelect = { onEvent(SelectOutputFormatEncodingType(it)) },
            combinedTestTag = TestTag.AudioOutputEncodingType,
            values = editData.audioFormatEncodingTypes
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
            selected = editData.audioOutputSampleRate,
            onSelect = { onEvent(SelectOutputFormatSampleRateType(it)) },
            combinedTestTag = TestTag.AudioOutputSampleRateType,
            values = editData.audioFormatSampleRateTypes
        )
    }
}