package org.rhasspy.mobile.services.httpclient

import org.koin.core.component.KoinComponent
import org.koin.core.component.get

enum class HttpClientPath(val path: String) : KoinComponent {
    PlayWav("api/play-wav"),
    TextToIntent("api/text-to-intent"),
    SpeechToText("api/speech-to-text"),
    TextToSpeech("api/text-to-speech");

    override fun toString(): String {
        return path
    }

    fun fromBaseConfiguration(): String {
        val params = get<HttpClientServiceParams>()
        return "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/$path"
    }
}