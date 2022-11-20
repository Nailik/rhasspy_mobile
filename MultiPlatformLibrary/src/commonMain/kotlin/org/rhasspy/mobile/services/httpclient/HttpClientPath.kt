package org.rhasspy.mobile.services.httpclient

enum class HttpClientPath(private val path: String) {
    PlayWav("//api/play-wav"),
    TextToIntent("//api/text-to-intent"),
    SpeechToText("//api/speech-to-text"),
    TextToSpeech("//api/text-to-speech");

    override fun toString(): String {
        return path
    }
}