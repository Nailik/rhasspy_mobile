package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiAction.Change
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiAction.Change.ToggleHttpSSLVerificationDisabled
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiAction.Change.UpdateHttpClientServerEndpointHost
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiAction.Change.UpdateHttpClientServerEndpointPort
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiAction.Change.UpdateHttpClientTimeout

class RemoteHermesHttpConfigurationViewModel(
    service: HttpClientService,
    testRunner: RemoteHermesHttpConfigurationTest
) : IConfigurationViewModel<RemoteHermesHttpConfigurationTest, RemoteHermesHttpConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::RemoteHermesHttpConfigurationViewState
) {

    fun onAction(action: RemoteHermesHttpConfigurationUiAction){
        when(action) {
            is Change -> onChange(action)
        }
    }

    private fun onChange(change: Change){
        contentViewState.update {
            when (change) {
                ToggleHttpSSLVerificationDisabled -> it.copy(isHttpSSLVerificationDisabled = !it.isHttpSSLVerificationDisabled)
                is UpdateHttpClientServerEndpointHost -> it.copy(httpClientServerEndpointHost = change.value)
                is UpdateHttpClientServerEndpointPort -> it.copy(httpClientServerEndpointPortText = change.value)
                is UpdateHttpClientTimeout -> it.copy(httpClientTimeoutText = change.value)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.httpClientServerEndpointHost.value = data.httpClientServerEndpointHost
        ConfigurationSetting.httpClientServerEndpointPort.value = data.httpClientServerEndpointPort
        ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = data.isHttpSSLVerificationDisabled
    }


    //test
    val isSpeechToTextTestVisible = combineState(
        ConfigurationSetting.speechToTextOption.data,
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == SpeechToTextOption.RemoteHTTP && !isUseCustomEndpoint
    }
    val isIntentRecognitionTestVisible = combineState(
        ConfigurationSetting.intentRecognitionOption.data,
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == IntentRecognitionOption.RemoteHTTP && !isUseCustomEndpoint
    }
    val isTextToSpeechTestVisible = combineState(
        ConfigurationSetting.textToSpeechOption.data,
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == TextToSpeechOption.RemoteHTTP && !isUseCustomEndpoint
    }

    val isRecordingAudio = testRunner.isRecording

    private val _testIntentRecognitionText = MutableStateFlow("")
    val testIntentRecognitionText = _testIntentRecognitionText.readOnly
    val isIntentRecognitionTestEnabled =
        _testIntentRecognitionText.mapReadonlyState { it.isNotEmpty() }

    private val _testTextToSpeechText = MutableStateFlow("")
    val testTextToSpeechText = _testTextToSpeechText.readOnly
    val isTextToSpeechTestEnabled = _testTextToSpeechText.mapReadonlyState { it.isNotEmpty() }

    //update intent test text
    fun updateTestIntentRecognitionText(text: String) {
        _testIntentRecognitionText.value = text
    }

    //update the test text
    fun updateTestTextToSpeechText(text: String) {
        _testTextToSpeechText.value = text
    }

    override fun initializeTestParams() {
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = data.isHttpSSLVerificationDisabled,
                    httpClientServerEndpointHost = data.httpClientServerEndpointHost,
                    httpClientServerEndpointPort = data.httpClientServerEndpointPort
                )
            )
        }
    }

    fun toggleRecording() = testRunner.toggleRecording()

    fun runIntentRecognitionTest() = testRunner.startIntentRecognitionTest(_testIntentRecognitionText.value)

    fun runTextToSpeechTest() = testRunner.startTextToSpeechTest(_testTextToSpeechText.value)

}