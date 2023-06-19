package org.rhasspy.mobile.logic.services.httpclient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class HttpClientServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<HttpClientServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.siteId.data,
                ConfigurationSetting.isHttpClientSSLVerificationDisabled.data,
                ConfigurationSetting.httpClientServerEndpointHost.data,
                ConfigurationSetting.httpClientServerEndpointPort.data,
                ConfigurationSetting.httpClientTimeout.data,
                ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data,
                ConfigurationSetting.speechToTextHttpEndpoint.data,
                ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.data,
                ConfigurationSetting.intentRecognitionHttpEndpoint.data,
                ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.data,
                ConfigurationSetting.textToSpeechHttpEndpoint.data,
                ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.data,
                ConfigurationSetting.audioPlayingHttpEndpoint.data,
                ConfigurationSetting.intentHandlingHttpEndpoint.data,
                ConfigurationSetting.intentHandlingHomeAssistantEndpoint.data,
                ConfigurationSetting.intentHandlingHomeAssistantAccessToken.data,
                ConfigurationSetting.intentHandlingOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): HttpClientServiceParams {
        return HttpClientServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            isHttpSSLVerificationDisabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
            httpClientServerEndpointHost = ConfigurationSetting.httpClientServerEndpointHost.value,
            httpClientServerEndpointPort = ConfigurationSetting.httpClientServerEndpointPort.value,
            httpClientTimeout = ConfigurationSetting.httpClientTimeout.value,
            isUseCustomSpeechToTextHttpEndpoint = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
            speechToTextHttpEndpoint = ConfigurationSetting.speechToTextHttpEndpoint.value,
            isUseCustomIntentRecognitionHttpEndpoint = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
            intentRecognitionHttpEndpoint = ConfigurationSetting.intentRecognitionHttpEndpoint.value,
            isUseCustomTextToSpeechHttpEndpoint = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
            textToSpeechHttpEndpoint = ConfigurationSetting.textToSpeechHttpEndpoint.value,
            isUseCustomAudioPlayingEndpoint = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
            audioPlayingHttpEndpoint = ConfigurationSetting.audioPlayingHttpEndpoint.value,
            intentHandlingHttpEndpoint = ConfigurationSetting.intentHandlingHttpEndpoint.value,
            intentHandlingHassEndpoint = ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value,
            intentHandlingHassAccessToken = ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value,
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value
        )
    }


}