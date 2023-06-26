package org.rhasspy.mobile.logic.services.httpclient

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class HttpClientServiceParamsCreator {

    operator fun invoke(): StateFlow<HttpClientServiceParams> {

        return combineStateFlow(
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
        ).mapReadonlyState {
            getParams()
        }

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