package org.rhasspy.mobile.logic.services.httpclient

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

enum class HttpClientPath(val path: String) : KoinComponent {
    PlayWav("api/play-wav"),
    TextToIntent("api/text-to-intent"),
    SpeechToText("api/speech-to-text"),
    TextToSpeech("api/text-to-speech");

    override fun toString(): String {
        return path
    }

    fun stringFromParams(params: HttpClientServiceParams): String {
        return "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/$path"
    }

    fun stringFromConfiguration(): String {
        return "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/$path"
    }

}