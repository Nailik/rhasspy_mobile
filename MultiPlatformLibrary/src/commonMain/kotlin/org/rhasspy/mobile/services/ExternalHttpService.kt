package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.bits.*
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.AudioStreamInterface
import org.rhasspy.mobile.settings.ConfigurationSettings

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api

/**
 * calls external http services
 */
object ExternalHttpService {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val httpClient = HttpClient() {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
    }

    fun speechToText() {
/*
/api/speech-to-text
POST a WAV file and have Rhasspy return the text transcription
Set Accept: application/json to receive JSON with more details
?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
 */
    }

    fun intentRecognition(text: String) {

        logger.i { text }

        /*
        /api/text-to-intent
POST text and have Rhasspy process it as command
Returns intent JSON when command has been processed
?nohass=true - stop Rhasspy from handling the intent
?entity=<entity>&value=<value> - set custom entity/value in recognized intent
         */
    }

    /**
     * api/text-to-speech
     * POST text and have Rhasspy speak it
     *  ?voice=<voice> - override default TTS voice
     * ?language=<language> - override default TTS language or locale
     * ?repeat=true - have Rhasspy repeat the last sentence it spoke
     * ?volume=<volume> - volume level to speak at (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun textToSpeech(text: String) {

        logger.v { "sending text to speech\nendpoint:\n${ConfigurationSettings.textToSpeechEndpoint.data}\ntext:\n$text" }

        try {

            val response = httpClient.post(
                url = Url(ConfigurationSettings.textToSpeechEndpoint.data)
            ) {
                setBody(text)
            }

            //receive the bytearray with audio data

            var player: AudioStreamInterface? = null
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                channel.read(desiredSize = 128) { memory: Memory, _: Long, _: Long ->

                    val data = ByteArray(memory.size32)
                    memory.copyTo(data, 0, memory.size32)

                    player?.also {
                        it.enqueue(data)
                    } ?: kotlin.run {
                        player = AudioPlayer.startStream(data)
                    }

                    memory.size32
                }
            }
            player?.close()

        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
        }
    }

    fun audioPlaying() {
/*
/api/play-wav
POST to play WAV data
Make sure to set Content-Type to audio/wav
?siteId=site1,site2,... to apply to specific site(s)
 */
    }

    fun intentHandling() {
/*
Rhasspy can POST the intent JSON to a remote URL.

Add to your profile:

"handle": {
  "system": "remote",
  "remote": {
      "url": "http://<address>:<port>/path/to/endpoint"
  }
}
When an intent is recognized, Rhasspy will POST to handle.remote.url with the intent JSON. Your server should return JSON back, optionally with additional information (see below).

Implemented by rhasspy-remote-http-hermes
 */
    }

}