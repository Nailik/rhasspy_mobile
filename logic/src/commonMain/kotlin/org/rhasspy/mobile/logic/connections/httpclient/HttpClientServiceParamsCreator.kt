package org.rhasspy.mobile.logic.connections.httpclient

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class HttpClientServiceParamsCreator {

    operator fun invoke(): StateFlow<HttpClientServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.siteId.data,
            //TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.data,
            //TODO ConfigurationSetting.httpClientServerEndpointHost.data,
            //TODO ConfigurationSetting.httpClientServerEndpointPort.data,
            //TODO ConfigurationSetting.httpClientTimeout.data,
            //TODO ConfigurationSetting.speechToTextHttpEndpoint.data,
            ConfigurationSetting.intentHandlingOption.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): HttpClientServiceParams {
        return HttpClientServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            isHttpSSLVerificationDisabled = false,//TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
            httpClientServerEndpointHost = "",//TODO ConfigurationSetting.httpClientServerEndpointHost.value,
            httpClientServerEndpointPort = 1,//TODO ConfigurationSetting.httpClientServerEndpointPort.value,
            httpClientTimeout = 5,//TODO ConfigurationSetting.httpClientTimeout.value,
            speechToTextHttpEndpoint = "",//TODO  ConfigurationSetting.speechToTextHttpEndpoint.value,
            intentRecognitionHttpEndpoint = "",//TODO  ConfigurationSetting.intentRecognitionHttpEndpoint.value,
            textToSpeechHttpEndpoint = "",//TODO  ConfigurationSetting.textToSpeechHttpEndpoint.value,
            audioPlayingHttpEndpoint = "",//TODO ConfigurationSetting.audioPlayingHttpEndpoint.value,
            intentHandlingHttpEndpoint = "",//TODO  ConfigurationSetting.intentHandlingHttpEndpoint.value,
            intentHandlingHomeAssistantEndpoint = "",//TODO  ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value,
            intentHandlingHomeAssistantAccessToken = "",//TODO ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value,
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value
        )
    }


}