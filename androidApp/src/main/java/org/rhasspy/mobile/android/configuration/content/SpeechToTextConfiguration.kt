package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
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
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.SpeechToTextConfigurationViewModel

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun SpeechToTextConfigurationContent(viewModel: SpeechToTextConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.SpeechToTextConfiguration),
        title = MR.strings.speechToText,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //drop down of option
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
                selected = viewModel.speechToTextOption.collectAsState().value,
                onSelect = viewModel::selectSpeechToTextOption,
                values = viewModel.speechToTextOptions
            ) {

                if (viewModel.isSpeechToTextHttpSettingsVisible(it)) {
                    SpeechToTextHTTP(viewModel)
                }

                if (viewModel.isSpeechToTextMqttSettingsVisible(it)) {
                    SpeechToTextMqtt(viewModel)
                }
            }
        }

    }

}

/**
 * http endpoint setings
 */
@Composable
private fun SpeechToTextHTTP(viewModel: SpeechToTextConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomSpeechToTextHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //input to edit http endpoint
        TextFieldListItem(
            enabled = viewModel.isSpeechToTextHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.speechToTextHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::updateSpeechToTextHttpEndpoint,
            label = translate(MR.strings.speechToTextURL, HttpClientPath.SpeechToText.path)
        )

    }

}

/**
 * mqtt silence detection settings
 */
@Composable
private fun SpeechToTextMqtt(viewModel: SpeechToTextConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use silence detection
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.MqttSilenceDetectionSwitch),
            text = MR.strings.useMqttSilenceDetection,
            isChecked = viewModel.isUseSpeechToTextMqttSilenceDetection.collectAsState().value,
            onCheckedChange = viewModel::toggleUseSpeechToTextMqttSilenceDetection
        )
    }

}

/**
 * microphone button to test speech to text
 */
@Composable
private fun TestContent(
    viewModel: SpeechToTextConfigurationViewModel
) {
    RequiresMicrophonePermission(MR.strings.microphonePermissionInfoRecord, viewModel::runTest) { onClick ->
        FilledTonalButtonListItem(
            text = if (viewModel.isRecordingAudio.collectAsState().value) MR.strings.stopRecordAudio else MR.strings.startRecordAudio,
            onClick = onClick
        )
    }
}