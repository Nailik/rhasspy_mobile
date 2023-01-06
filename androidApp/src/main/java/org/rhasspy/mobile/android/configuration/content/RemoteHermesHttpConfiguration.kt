package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.configuration.RemoteHermesHttpConfigurationViewModel

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.RemoteHermesHttpConfiguration),
        title = MR.strings.remoteHermesHTTP,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //base http endpoint
            TextFieldListItem(
                label = MR.strings.baseHost,
                modifier = Modifier.testTag(TestTag.Host),
                value = viewModel.httpClientServerEndpointHost.collectAsState().value,
                onValueChange = viewModel::updateHttpClientServerEndpointHost,
                isLastItem = false
            )
        }

        item {
            //port
            TextFieldListItem(
                label = MR.strings.port,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag(TestTag.Port),
                value = viewModel.httpClientServerEndpointPort.collectAsState().value,
                onValueChange = viewModel::updateHttpClientServerEndpointPort,
            )
        }

        item {
            //timeout
            TextFieldListItem(
                label = MR.strings.requestTimeout,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag(TestTag.Timeout),
                value = viewModel.httpClientTimeoutText.collectAsState().value,
                onValueChange = viewModel::updateHttpClientTimeout,
            )
        }

        item {
            //switch to toggle validation of SSL certificate
            SwitchListItem(
                text = MR.strings.disableSSLValidation,
                modifier = Modifier.testTag(TestTag.SSLSwitch),
                secondaryText = MR.strings.disableSSLValidationInformation,
                isChecked = viewModel.isHttpSSLVerificationDisabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHttpSSLVerificationDisabled
            )
        }

    }

}

/**
 * test http connection button
 */
@Composable
private fun TestContent(viewModel: RemoteHermesHttpConfigurationViewModel) {

    Column {

        if(viewModel.isSpeechToTextTestVisible.collectAsState().value) {
            RequiresMicrophonePermission(MR.strings.microphonePermissionInfoRecord, viewModel::runSpeechToTextTest) { onClick ->
                FilledTonalButtonListItem(
                    text = if (viewModel.isRecordingAudio.collectAsState().value) MR.strings.stopRecordAudio else MR.strings.startRecordAudio,
                    onClick = onClick
                )
            }
        }

        if(viewModel.isIntentRecognitionTestVisible.collectAsState().value) {
            TextFieldListItem(
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = viewModel.testIntentRecognitionText.collectAsState().value,
                onValueChange = viewModel::updateTestIntentRecognitionText,
                label = MR.strings.textIntentRecognitionText
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeIntentRecognition,
                enabled = viewModel.isIntentRecognitionTestEnabled.collectAsState().value,
                onClick = viewModel::runIntentRecognitionTest
            )
        }

        if(viewModel.isTextToSpeechTestVisible.collectAsState().value){
            TextFieldListItem(
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = viewModel.testTextToSpeechText.collectAsState().value,
                onValueChange = viewModel::updateTestTextToSpeechText,
                label = MR.strings.textToSpeechText
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeTextToSpeechText,
                enabled = viewModel.isTextToSpeechTestEnabled.collectAsState().value,
                onClick = viewModel::runTextToSpeechTest
            )
        }
    }
}