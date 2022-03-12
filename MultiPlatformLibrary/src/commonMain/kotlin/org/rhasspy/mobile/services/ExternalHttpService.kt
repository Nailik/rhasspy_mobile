package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.rhasspy.mobile.data.IntentHandlingOptions
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

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    suspend fun speechToText(data: ByteArray) {

        logger.v { "sending speechToText \nendpoint:\n${ConfigurationSettings.speechToTextHttpEndpoint.data}\ndata:\n${data.size}" }

        try {
            val response = httpClient.post(
                url = Url("${ConfigurationSettings.speechToTextHttpEndpoint.data}?noheader=true")
            ) {
                setBody(data)
            }

            val text = response.bodyAsText()

            logger.v { "speechToText received:\n$text" }

            ServiceInterface.receivedTextFromSpeech(text)

        } catch (e: Exception) {
            logger.e(e) { "sending speechToText Exception" }
        }
    }


    /**
     * /api/text-to-intent
     * POST text and have Rhasspy process it as command
     * Returns intent JSON when command has been processed
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?entity=<entity>&value=<value> - set custom entity/value in recognized intent
     */
    suspend fun intentRecognition(text: String) {

        logger.v { "sending intentRecognition text\nendpoint:\n${ConfigurationSettings.intentRecognitionEndpoint.data}\ntext:\n$text" }

        try {
            //check handled directly while recognition
            val handleDirectly = ConfigurationSettings.intentHandlingOption.data == IntentHandlingOptions.WithRecognition

            logger.v { "intent will be handled directly $handleDirectly" }

            val response = httpClient.post(
                url = Url(
                    "${ConfigurationSettings.intentRecognitionEndpoint.data}${
                        if (!handleDirectly) {
                            "?nohass=true"
                        } else ""
                    }"
                )
            ) {
                setBody(text)
            }

            val intent = response.bodyAsText()

            logger.v { "intentRecognition received:\n$intent" }

            if (!handleDirectly) {
                ServiceInterface.intentHandling(intent)
            }

        } catch (e: Exception) {
            logger.e(e) { "sending intentRecognition Exception" }
        }
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

            logger.v { "textToSpeech received Data" }

            ServiceInterface.playAudio(response.body())

        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: ByteArray) {

        logger.v { "sending audio \nendpoint:\n${ConfigurationSettings.audioPlayingEndpoint.data}\ndata:\n${data.size}" }

        try {
            val response = httpClient.post(
                url = Url(ConfigurationSettings.audioPlayingEndpoint.data)
            ) {
                setAttributes {
                    contentType(ContentType("audio", "wav"))
                }
                setBody(data)
            }

            logger.v { "sending audio received:\n${response.bodyAsText()}" }

        } catch (e: Exception) {
            logger.e(e) { "sending audio Exception" }
        }
    }


    /**
     * Rhasspy can POST the intent JSON to a remote URL.
     *
     * Add to your profile:
     *
     * "handle": {
     *  "system": "remote",
     *  "remote": {
     *      "url": "http://<address>:<port>/path/to/endpoint"
     *   }
     * }
     * When an intent is recognized, Rhasspy will POST to handle.remote.url with the intent JSON.
     * Your server should return JSON back, optionally with additional information (see below).
     *
     * Implemented by rhasspy-remote-http-hermes
     */
    suspend fun intentHandling(intent: String) {

        logger.v { "sending intentHandling\nendpoint:\n${ConfigurationSettings.intentHandlingEndpoint.data}\nintent:\n$intent" }

        try {

            val response = httpClient.post(
                url = Url(ConfigurationSettings.intentHandlingEndpoint.data)
            ) {
                setBody(intent)
            }

            logger.v { "sending intent received:\n${response.bodyAsText()}" }

        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
        }
    }

}