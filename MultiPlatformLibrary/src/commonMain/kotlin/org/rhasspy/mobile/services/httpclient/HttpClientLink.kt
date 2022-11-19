package org.rhasspy.mobile.services.httpclient

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.nativeutils.configureEngine
import org.rhasspy.mobile.services.IServiceLink
import org.rhasspy.mobile.services.httpclient.data.HttpClientCallType
import org.rhasspy.mobile.services.httpclient.data.HttpClientResponse

class HttpClientLink(
    private val isHttpSSLVerificationDisabled: Boolean,
    private val speechToTextHttpEndpoint: String,
    private val intentRecognitionHttpEndpoint: String,
    private val isHandleIntentDirectly: Boolean,
    private val textToSpeechHttpEndpoint: String,
    private val audioPlayingHttpEndpoint: String,
    private val intentHandlingHttpEndpoint: String,
    private val intentHandlingHassEndpoint: String,
    private val intentHandlingHassAccessToken: String,
) : IServiceLink {

    private val logger = Logger.withTag("HttpClientLink")

    private lateinit var httpClient: HttpClient

    override fun start(scope: CoroutineScope) {
        httpClient = HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
            }
            engine {
                configureEngine(isHttpSSLVerificationDisabled)
            }
        }
    }

    override fun destroy() {
        if (::httpClient.isInitialized) {
            httpClient.cancel()
        }
    }

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    suspend fun speechToText(data: List<Byte>): HttpClientResponse<String> {
        val callType = HttpClientCallType.SpeechToText

        logger.v { "sending speechToText \nendpoint:\n$speechToTextHttpEndpoint\ndata:\n${data.size}" }

        return try {
            val request = httpClient.post(
                url = Url("$speechToTextHttpEndpoint?noheader=true")
            ) {
                setBody(data.toByteArray())
            }

            val response = request.body<String>()
            logger.v { "speechToText received:\n$response" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending speechToText Exception" }
            HttpClientResponse.HttpClientError(e, callType)
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
    suspend fun intentRecognition(text: String): HttpClientResponse<String?> {
        val callType = HttpClientCallType.IntentRecognition

        logger.v { "sending intentRecognition text\nendpoint:\n$intentRecognitionHttpEndpoint\ntext:\n$text" }

        return try {
            logger.v { "intent will be handled directly $isHandleIntentDirectly" }

            val request = httpClient.post(
                url = Url(
                    "$intentRecognitionHttpEndpoint${
                        if (!isHandleIntentDirectly) {
                            "?nohass=true"
                        } else ""
                    }"
                )
            ) {
                setBody(text)
            }

            val response = request.body<String>()

            //return only intent
            //no intent:
            val data = if (Json.decodeFromString<JsonObject>(response)["intent"]?.jsonObject?.get("name")?.jsonPrimitive.toString().isNotEmpty()) {
                //there was an intent found, return json
                response
            } else {
                //there was no intent found, return null
                null
            }
            logger.v { "intentRecognition received:\n$data" }
            HttpClientResponse.HttpClientSuccess(data, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending intentRecognition Exception" }
            HttpClientResponse.HttpClientError(e, callType)
        }
    }

    /**
     * api/text-to-speech
     * POST text and have Rhasspy speak it
     * ?voice=<voice> - override default TTS voice
     * ?language=<language> - override default TTS language or locale
     * ?repeat=true - have Rhasspy repeat the last sentence it spoke
     * ?volume=<volume> - volume level to speak at (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun textToSpeech(text: String): HttpClientResponse<ByteArray> {
        val callType = HttpClientCallType.TextToSpeech

        logger.v { "sending text to speech\nendpoint:\n$textToSpeechHttpEndpoint\ntext:\n$text" }

        return try {
            val request = httpClient.post(
                url = Url(textToSpeechHttpEndpoint)
            ) {
                setBody(text)
            }

            val response = request.body<ByteArray>()

            logger.v { "textToSpeech received Data" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending text to speech Exception" }
            HttpClientResponse.HttpClientError(e, callType)
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>): HttpClientResponse<String> {
        val callType = HttpClientCallType.PlayWav

        logger.v { "sending audio \nendpoint:\n$audioPlayingHttpEndpoint data:\n${data.size}" }

        return try {
            val request = httpClient.post(
                url = Url(audioPlayingHttpEndpoint)
            ) {
                setAttributes {
                    contentType(ContentType("audio", "wav"))
                }
                setBody(data.toByteArray())
            }

            val response = request.body<String>()
            logger.v { "sending audio received:\n${response}" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending audio Exception" }
            HttpClientResponse.HttpClientError(e, callType)
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
    suspend fun intentHandling(intent: String): HttpClientResponse<String> {
        val callType = HttpClientCallType.IntentHandling

        logger.v { "sending intentHandling\nendpoint:\n$intentHandlingHttpEndpoint\nintent:\n$intent" }

        return try {

            val request = httpClient.post(
                url = Url(intentHandlingHttpEndpoint)
            ) {
                setBody(intent)
            }

            val response = request.body<String>()
            logger.v { "sending intent received:\n${response}" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending intentHandling Exception" }
            HttpClientResponse.HttpClientError(e, callType)
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): HttpClientResponse<String> {
        val callType = HttpClientCallType.HassEvent

        logger.v {
            "sending intent as Event to Home Assistant\nendpoint:\n$intentHandlingHassEndpoint/api/events/rhasspy_$intentName\nintent:\n$json"
        }

        return try {

            val url = "$intentHandlingHassEndpoint/api/events/rhasspy_$intentName"

            logger.v { "complete endpoint url" }

            val request = httpClient.post(
                url = Url(url)
            ) {
                buildHeaders {
                    header("Authorization", "Bearer $intentHandlingHassAccessToken")
                    contentType(ContentType("application", "json"))
                }
                setBody(json)
            }

            val response = request.body<String>()
            logger.v { "sending intent received:\n${response}" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending hassEvent Exception" }
            HttpClientResponse.HttpClientError(e, callType)
        }

    }


    /**
     * send intent as Intent to Home Assistant
     */
    suspend fun hassIntent(intentJson: String): HttpClientResponse<String> {
        val callType = HttpClientCallType.HassIntent

        logger.v { "sending intent as Intent to Home Assistant\nendpoint:\n$intentHandlingHassEndpoint/api/intent/handle\nintent:\n$intentJson" }

        return try {

            val request = httpClient.post(
                url = Url("$intentHandlingHassEndpoint/api/intent/handle")
            ) {
                buildHeaders {
                    header("Authorization", "Bearer $intentHandlingHassAccessToken")
                    contentType(ContentType("application", "json"))
                }
                setBody(intentJson)
            }

            val response = request.body<String>()
            logger.v { "sending intent received:\n${response}" }
            HttpClientResponse.HttpClientSuccess(response, callType)
        } catch (e: Exception) {

            logger.e(e) { "sending hassIntent Exception" }
            HttpClientResponse.HttpClientError(e, callType)
        }
    }
}