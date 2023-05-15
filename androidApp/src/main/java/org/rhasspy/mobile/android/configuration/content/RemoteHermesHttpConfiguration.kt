package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.android.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.Screen.ConfigurationScreen.ConfigurationDetailScreen.RemoteHermesHttpConfigurationScreen

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun RemoteHermesHttpConfigurationContent(screen: RemoteHermesHttpConfigurationScreen) {
    val viewModel: RemoteHermesHttpConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenType = screen.type,
        config = ConfigurationScreenConfig(MR.strings.remoteHermesHTTP.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) },
        testContent = {
            TestContent(
                testIntentRecognitionText = contentViewState.testIntentRecognitionText,
                testTextToSpeechText = contentViewState.testTextToSpeechText,
                isTestRecordingAudio = contentViewState.isTestRecordingAudio,
                isSpeechToTextTestVisible = contentViewState.isSpeechToTextTestVisible,
                isIntentRecognitionTestVisible = contentViewState.isIntentRecognitionTestVisible,
                isTextToSpeechTestVisible = contentViewState.isTextToSpeechTestVisible,
                onEvent = viewModel::onEvent
            )
        }
    ) {

        item {
            //base http endpoint
            TextFieldListItem(
                label = MR.strings.baseHost.stable,
                modifier = Modifier.testTag(TestTag.Host),
                value = contentViewState.httpClientServerEndpointHost,
                onValueChange = { viewModel.onEvent(UpdateHttpClientServerEndpointHost(it)) },
                isLastItem = false
            )
        }

        item {
            //port
            TextFieldListItem(
                label = MR.strings.port.stable,
                modifier = Modifier.testTag(TestTag.Port),
                value = contentViewState.httpClientServerEndpointPortText,
                onValueChange = { viewModel.onEvent(UpdateHttpClientServerEndpointPort(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //timeout
            TextFieldListItem(
                label = MR.strings.requestTimeout.stable,
                modifier = Modifier.testTag(TestTag.Timeout),
                value = contentViewState.httpClientTimeoutText,
                onValueChange = { viewModel.onEvent(UpdateHttpClientTimeout(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //switch to toggle validation of SSL certificate
            SwitchListItem(
                text = MR.strings.disableSSLValidation.stable,
                modifier = Modifier.testTag(TestTag.SSLSwitch),
                secondaryText = MR.strings.disableSSLValidationInformation.stable,
                isChecked = contentViewState.isHttpSSLVerificationDisabled,
                onCheckedChange = { viewModel.onEvent(SetHttpSSLVerificationDisabled(it)) },
            )
        }

    }

}

/**
 * test http connection button
 */
@Composable
private fun TestContent(
    testIntentRecognitionText: String,
    testTextToSpeechText: String,
    isTestRecordingAudio: Boolean,
    isSpeechToTextTestVisible: Boolean,
    isIntentRecognitionTestVisible: Boolean,
    isTextToSpeechTestVisible: Boolean,
    onEvent: (RemoteHermesHttpConfigurationUiEvent) -> Unit
) {

    Column {

        if (isSpeechToTextTestVisible) {
            RequiresMicrophonePermission(
                informationText = MR.strings.microphonePermissionInfoRecord.stable,
                onClick = { onEvent(TestRemoteHermesHttpToggleRecording) }
            ) { onClick ->
                FilledTonalButtonListItem(
                    text = if (isTestRecordingAudio) MR.strings.stopRecordAudio.stable else MR.strings.startRecordAudio.stable,
                    onClick = onClick
                )
            }
        }

        if (isIntentRecognitionTestVisible) {
            TextFieldListItem(
                label = MR.strings.textIntentRecognitionText.stable,
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = testIntentRecognitionText,
                onValueChange = { onEvent(UpdateTestRemoteHermesHttpIntentRecognitionText(it)) }
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeIntentRecognition.stable,
                onClick = { onEvent(TestRemoteHermesHttpIntentRecognitionTest) }
            )
        }

        if (isTextToSpeechTestVisible) {
            TextFieldListItem(
                label = MR.strings.textToSpeechText.stable,
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = testTextToSpeechText,
                onValueChange = { onEvent(UpdateTestRemoteHermesHttpTextToSpeechText(it)) }
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeTextToSpeechText.stable,
                onClick = { onEvent(TestRemoteHermesHttpTextToSpeechTest) }
            )
        }
    }

}