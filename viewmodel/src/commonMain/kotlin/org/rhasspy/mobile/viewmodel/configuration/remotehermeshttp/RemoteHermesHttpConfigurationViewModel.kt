package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.RemoteHermesHttpConfigurationScreenDestination.EditScreen

@Stable
class RemoteHermesHttpConfigurationViewModel(
    service: HttpClientService
) : IConfigurationViewModel<RemoteHermesHttpConfigurationViewState>(
    service = service,
    initialViewState = ::RemoteHermesHttpConfigurationViewState
) {

    val screen = navigator.topScreen(EditScreen)

    fun onEvent(event: RemoteHermesHttpConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        updateViewState {
            when (change) {
                is SetHttpSSLVerificationDisabled -> it.copy(isHttpSSLVerificationDisabled = change.disabled)
                is UpdateHttpClientServerEndpointHost -> it.copy(httpClientServerEndpointHost = change.host)
                is UpdateHttpClientServerEndpointPort -> it.copy(httpClientServerEndpointPortText = change.port)
                is UpdateHttpClientTimeout -> it.copy(httpClientTimeoutText = change.text)
                is UpdateTestRemoteHermesHttpIntentRecognitionText -> it.copy(testIntentRecognitionText = change.text)
                is UpdateTestRemoteHermesHttpTextToSpeechText -> it.copy(testTextToSpeechText = change.text)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            TestRemoteHermesHttpIntentRecognitionTest -> toggleRecording()
            TestRemoteHermesHttpTextToSpeechTest -> startIntentRecognitionTest()
            TestRemoteHermesHttpToggleRecording -> startTextToSpeechTest()
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.httpClientServerEndpointHost.value = data.httpClientServerEndpointHost
        ConfigurationSetting.httpClientServerEndpointPort.value = data.httpClientServerEndpointPort
        ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = data.isHttpSSLVerificationDisabled
    }

    private fun toggleRecording() {
        testScope.launch(Dispatchers.IO) {
            get<RecordingService>().isRecording.collect { isRecording ->
                updateViewState {
                    it.copy(isTestRecordingAudio = isRecording)
                }
            }
        }

        testScope.launch {
            if (get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            if (!get<RecordingService>().isRecording.value) {
                println("not yet recording start")
                //start recording
                get<SpeechToTextService>().startSpeechToText("", false)
            } else {
                println("is recording, stop")
                //stop recording
                get<SpeechToTextService>().endSpeechToText("", false)
            }
        }
    }

    private fun startIntentRecognitionTest() {
        testScope.launch {
            get<HttpClientService>().recognizeIntent(data.testIntentRecognitionText)
        }
    }

    private fun startTextToSpeechTest() {
        testScope.launch {
            val result = get<HttpClientService>().textToSpeech(data.testTextToSpeechText)
            if (result is HttpClientResult.Success) {
                @Suppress("DEPRECATION")
                get<AudioPlayingService>().playAudio(AudioSource.Data(result.data))
            }
        }
    }

}