package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.TestSpeechToTextToggleRecording
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.UpdateSpeechToTextHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun SpeechToTextConfigurationContent(viewModel: SpeechToTextConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.SpeechToTextConfiguration),
        config = ConfigurationScreenConfig(MR.strings.speechToText.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) },
        onConsumed = { viewModel.onConsumed(it) },
        testContent = {
            TestContent(
                isRecordingAudio = contentViewState.isTestRecordingAudio,
                onEvent = viewModel::onEvent
            )
        }
    ) {

        item {
            SpeechToTextOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onEvent
            )
        }

    }

}

@Composable
private fun SpeechToTextOptionContent(
    viewState: SpeechToTextConfigurationViewState,
    onAction: (SpeechToTextConfigurationUiEvent) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
        selected = viewState.speechToTextOption,
        onSelect = { onAction(SelectSpeechToTextOption(it)) },
        values = viewState.speechToTextOptions
    ) {

        when (it) {
            SpeechToTextOption.RemoteHTTP -> SpeechToTextHTTP(
                isUseCustomSpeechToTextHttpEndpoint = viewState.isUseCustomSpeechToTextHttpEndpoint,
                speechToTextHttpEndpoint = viewState.speechToTextHttpEndpoint,
                onAction = onAction
            )

            SpeechToTextOption.RemoteMQTT -> SpeechToTextMqtt(
                isUseSpeechToTextMqttSilenceDetection = viewState.isUseSpeechToTextMqttSilenceDetection,
                onAction = onAction
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
    speechToTextHttpEndpoint: String,
    onAction: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomSpeechToTextHttpEndpoint,
            onCheckedChange = { onAction(SetUseCustomHttpEndpoint(it)) }
        )

        //input to edit http endpoint
        TextFieldListItem(
            enabled = isUseCustomSpeechToTextHttpEndpoint,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = speechToTextHttpEndpoint,
            onValueChange = { onAction(UpdateSpeechToTextHttpEndpoint(it)) },
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
    onAction: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use silence detection
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.MqttSilenceDetectionSwitch),
            text = MR.strings.useMqttSilenceDetection.stable,
            isChecked = isUseSpeechToTextMqttSilenceDetection,
            onCheckedChange = { onAction(SetUseSpeechToTextMqttSilenceDetection(it)) }
        )
    }

}

/**
 * microphone button to test speech to text
 */
@Composable
private fun TestContent(
    isRecordingAudio: Boolean,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    RequiresMicrophonePermission(
        informationText = MR.strings.microphonePermissionInfoRecord.stable,
        onClick = { onEvent(TestSpeechToTextToggleRecording)}
    ) { onClick ->
        FilledTonalButtonListItem(
            text = if (isRecordingAudio) MR.strings.stopRecordAudio.stable else MR.strings.startRecordAudio.stable,
            onClick = onClick
        )
    }

}