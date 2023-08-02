package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.OpenAudioOutputFormat
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.OpenAudioRecorderFormat
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioOutputFormatConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioRecorderFormatConfigurationData

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextConfigurationScreen() {

    val viewModel: SpeechToTextConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.speechToText.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        SpeechToTextOptionEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun SpeechToTextOptionEditContent(
    editData: SpeechToTextConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            SpeechToTextOption(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}


@Composable
private fun SpeechToTextOption(
    editData: SpeechToTextConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
        selected = editData.speechToTextOption,
        onSelect = { onEvent(SelectSpeechToTextOption(it)) },
        values = editData.speechToTextOptions
    ) {

        when (it) {
            SpeechToTextOption.RemoteHTTP -> SpeechToTextHTTP(
                isUseCustomSpeechToTextHttpEndpoint = editData.isUseCustomSpeechToTextHttpEndpoint,
                speechToTextHttpEndpointText = editData.speechToTextHttpEndpointText,
                speechToTextAudioRecorderFormatData = editData.speechToTextAudioRecorderFormatData,
                speechToTextAudioOutputFormatData = editData.speechToTextAudioOutputFormatData,
                onEvent = onEvent
            )

            SpeechToTextOption.RemoteMQTT -> SpeechToTextMqtt(
                isUseSpeechToTextMqttSilenceDetection = editData.isUseSpeechToTextMqttSilenceDetection,
                onEvent = onEvent,
                speechToTextAudioRecorderFormatData = editData.speechToTextAudioRecorderFormatData,
                speechToTextAudioOutputFormatData = editData.speechToTextAudioOutputFormatData,
            )

            else                          -> Unit
        }

    }
}

/**
 * http endpoint setings
 */
@Composable
private fun SpeechToTextHTTP(
    isUseCustomSpeechToTextHttpEndpoint: Boolean,
    speechToTextHttpEndpointText: String,
    speechToTextAudioRecorderFormatData: SpeechToTextAudioRecorderFormatConfigurationData,
    speechToTextAudioOutputFormatData: SpeechToTextAudioOutputFormatConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomSpeechToTextHttpEndpoint,
            onCheckedChange = { onEvent(SetUseCustomHttpEndpoint(it)) }
        )

        //input to edit http endpoint
        TextFieldListItem(
            enabled = isUseCustomSpeechToTextHttpEndpoint,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = speechToTextHttpEndpointText,
            onValueChange = { onEvent(UpdateSpeechToTextHttpEndpoint(it)) },
            label = translate(MR.strings.speechToTextURL.stable, HttpClientPath.SpeechToText.path)
        )


        //button to open audio recorder format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenAudioRecorderFormat) },
            text = { Text(resource = MR.strings.audioRecorderFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioRecorderFormatData.audioRecorderChannelType.text)} | " +
                        "${translate(speechToTextAudioRecorderFormatData.audioRecorderEncodingType.text)} | " +
                        translate(speechToTextAudioRecorderFormatData.audioRecorderSampleRateType.text)
                Text(text = text)
            }
        )

        //button to open audio output format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenAudioOutputFormat) },
            text = { Text(MR.strings.audioOutputFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioOutputFormatData.audioOutputChannelType.text)} | " +
                        "${translate(speechToTextAudioOutputFormatData.audioOutputEncodingType.text)} | " +
                        translate(speechToTextAudioOutputFormatData.audioOutputSampleRateType.text)
                Text(text = text)
            }
        )
    }

}

/**
 * mqtt silence detection settings
 */
@Composable
private fun SpeechToTextMqtt(
    isUseSpeechToTextMqttSilenceDetection: Boolean,
    speechToTextAudioRecorderFormatData: SpeechToTextAudioRecorderFormatConfigurationData,
    speechToTextAudioOutputFormatData: SpeechToTextAudioOutputFormatConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use silence detection
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.MqttSilenceDetectionSwitch),
            text = MR.strings.useMqttSilenceDetection.stable,
            isChecked = isUseSpeechToTextMqttSilenceDetection,
            onCheckedChange = { onEvent(SetUseSpeechToTextMqttSilenceDetection(it)) }
        )

        //button to open audio recorder format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenAudioRecorderFormat) },
            text = { Text(MR.strings.audioRecorderFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioRecorderFormatData.audioRecorderChannelType.text)} | " +
                        "${translate(speechToTextAudioRecorderFormatData.audioRecorderEncodingType.text)} | " +
                        translate(speechToTextAudioRecorderFormatData.audioRecorderSampleRateType.text)
                Text(text = text)
            }
        )

        //button to open audio output format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenAudioOutputFormat) },
            text = { Text(MR.strings.audioOutputFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(speechToTextAudioOutputFormatData.audioOutputChannelType.text)} | " +
                        "${translate(speechToTextAudioOutputFormatData.audioOutputEncodingType.text)} | " +
                        translate(speechToTextAudioOutputFormatData.audioOutputSampleRateType.text)
                Text(text = text)
            }
        )

    }

}