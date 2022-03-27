package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.auth.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * calls external http services
 */
object HttpService {
    private val logger = Logger.withTag("HttpService")

    private val httpClient = HttpClient {
        expectSuccess = true
        install(WebSockets)
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
    suspend fun speechToText(data: List<Byte>): String? {

        logger.v { "sending speechToText \nendpoint:\n${ConfigurationSettings.speechToTextHttpEndpoint.data}\ndata:\n${data.size}" }

        return try {
            val response = httpClient.post<String>(
                url = Url(ConfigurationSettings.speechToTextHttpEndpoint.data)
            ) {
                body = data.toByteArray()
            }

            logger.v { "speechToText received:\n$response" }

            response

        } catch (e: Exception) {
            logger.e(e) { "sending speechToText Exception" }
            null
        }
    }


    /**
     * /api/text-to-intent
     * POST text and have Rhasspy process it as command
     * Returns intent JSON when command has been processed
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?entity=<entity>&value=<value> - set custom entity/value in recognized intent
     *
     * returns null if the intent is not found
     */
    suspend fun intentRecognition(text: String, handleDirectly: Boolean): String? {

        logger.v { "sending intentRecognition text\nendpoint:\n${ConfigurationSettings.intentRecognitionEndpoint.data}\ntext:\n$text" }

        return try {
            logger.v { "intent will be handled directly $handleDirectly" }

            val response = httpClient.post<String>(
                url = Url(
                    "${ConfigurationSettings.intentRecognitionEndpoint.data}${
                        if (!handleDirectly) {
                            "?nohass=true"
                        } else ""
                    }"
                )
            ) {
                body = text
            }

            logger.v { "intentRecognition received:\n$response" }

            //return only intent
            //no intent:
            return if (Json.decodeFromString<JsonObject>(response)["intent"]?.jsonObject?.get("name")?.jsonPrimitive.toString().isNotEmpty()) {
                //there was an intent found, return json
                response
            } else {
                //there was no intent found, return null
                null
            }
        } catch (e: Exception) {
            logger.e(e) { "sending intentRecognition Exception" }
            null
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
    suspend fun textToSpeech(text: String): List<Byte>? {

        logger.v { "sending text to speech\nendpoint:\n${ConfigurationSettings.textToSpeechEndpoint.data}\ntext:\n$text" }

        return try {

            val response = httpClient.post<List<Byte>>(
                url = Url(ConfigurationSettings.textToSpeechEndpoint.data)
            ) {
                body = text
            }

            logger.v { "textToSpeech received Data" }

            response
        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
            null
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>) {

        logger.v { "sending audio \nendpoint:\n${ConfigurationSettings.audioPlayingEndpoint.data}\ndata:\n${data.size}" }

        try {
            val response = httpClient.post<String>(
                url = Url(ConfigurationSettings.audioPlayingEndpoint.data)
            ) {
                setAttributes {
                    contentType(ContentType("audio", "wav"))
                }
                body = data.toByteArray()
            }

            logger.v { "sending audio received:\n${response}" }

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

            val response = httpClient.post<String>(
                url = Url(ConfigurationSettings.intentHandlingEndpoint.data)
            ) {
                body = intent
            }

            logger.v { "sending intent received:\n${response}" }

        } catch (e: Exception) {
            logger.e(e) { "sending intent Exception" }
        }
    }


    suspend fun hassEvent(json: String, intentName: String) {

        logger.v {
            "sending intent as Event to Home Assistant\nendpoint:\n${ConfigurationSettings.intentHandlingHassUrl.data}/api/events/rhasspy_$intentName\nintent:\n$json"
        }

        try {

            val url = "${ConfigurationSettings.intentHandlingHassUrl.data}/api/events/rhasspy_$intentName"

            logger.v { "complete endpoint url" }

            val response = httpClient.post<String>(
                url = Url(url)
            ) {
                buildHeaders {
                    header("Authorization", "Bearer ${ConfigurationSettings.intentHandlingHassAccessToken.data}")
                    contentType(ContentType("application", "json"))
                }
                body = json
            }

            logger.v { "sending intent received:\n${response}" }

        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
        }

    }


    suspend fun hassIntent(intent: String) {

        logger.v { "sending intent as Intent to Home Assistant\nendpoint:\n${ConfigurationSettings.intentHandlingHassUrl.data}/api/intent/handle\nintent:\n$intent" }

        try {

            val response = httpClient.post<String>(
                url = Url("${ConfigurationSettings.intentHandlingHassUrl.data}/api/intent/handle")
            ) {
                buildHeaders {
                    parseAuthorizationHeader("Bearer ${ConfigurationSettings.intentHandlingHassAccessToken.data}")
                    contentType(ContentType("application", "json"))
                }
                body = intent
            }

            logger.v { "sending intent received:\n${response}" }

        } catch (e: Exception) {
            logger.e(e) { "sending text to speech Exception" }
        }
    }

}