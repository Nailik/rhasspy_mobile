package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Action.TestSpeechToTextToggleRecording
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextConfigurationContent() {
    val viewModel: SpeechToTextConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()
        val screen by viewModel.screen.collectAsState()
        val contentViewState by viewState.editViewState.collectAsState()

        ConfigurationScreenItemContent(
            modifier = Modifier.testTag(SpeechToTextConfigurationScreen),
            screenType = screen.destinationType,
            config = ConfigurationScreenConfig(MR.strings.speechToText.stable),
            viewState = viewState,
            onAction = viewModel::onAction,
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
                speechToTextHttpEndpointText = viewState.speechToTextHttpEndpointText,
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
    speechToTextHttpEndpointText: String,
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
            value = speechToTextHttpEndpointText,
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
    FilledTonalButtonListItem(
        text = if (isRecordingAudio) MR.strings.stopRecordAudio.stable else MR.strings.startRecordAudio.stable,
        onClick = { onEvent(TestSpeechToTextToggleRecording) }
    )
}