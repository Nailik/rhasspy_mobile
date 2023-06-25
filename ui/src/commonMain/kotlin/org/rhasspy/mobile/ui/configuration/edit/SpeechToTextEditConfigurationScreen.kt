package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemEdit
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextEditConfigurationScreen() {

    val viewModel: SpeechToTextConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationEditViewState.collectAsState()

    ConfigurationScreenItemEdit(
        modifier = Modifier,
        kViewModel = viewModel,
        title = MR.strings.webserver.stable,
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
                onEvent = onEvent
            )

            SpeechToTextOption.RemoteMQTT -> SpeechToTextMqtt(
                isUseSpeechToTextMqttSilenceDetection = editData.isUseSpeechToTextMqttSilenceDetection,
                onEvent = onEvent
            )

            else -> {}
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

    }

}

/**
 * mqtt silence detection settings
 */
@Composable
private fun SpeechToTextMqtt(
    isUseSpeechToTextMqttSilenceDetection: Boolean,
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
    }

}