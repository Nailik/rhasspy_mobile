package org.rhasspy.mobile.ui.configuration.domains.mic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewState.AudioOutputFormatConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioOutputFormatScreen

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioOutputFormatConfigurationScreen(viewModel: AudioOutputFormatConfigurationViewModel) {

    ScreenContent(
        modifier = Modifier.testTag(AudioOutputFormatScreen),
        title = MR.strings.audioOutput.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

}