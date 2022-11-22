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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.logger.EventType
import org.rhasspy.mobile.nativeutils.configureEngine
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
class HttpClientService : IService() {

    private val params by inject<HttpClientServiceParams>()


    private val logger = Logger.withTag("HttpClientService")
    private val eventLogger by inject<EventLogger>(named(EventTag.HttpClientService.name))

    private var httpClient: HttpClient? = null

    /**
     * starts client and updates event
     */
    init {
        val startEvent = eventLogger.event(EventType.HttpClientStart)

        //starting
        startEvent.loading()
        try {
            httpClient = HttpClient(CIO) {
                expectSuccess = true
                install(WebSockets)
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                }
                engine {
                    configureEngine(params.isHttpSSLVerificationDisabled)
                }
            }
            //successfully start
            startEvent.success()
        } catch (e: Exception) {
            //start error
            startEvent.error(e)
        }
    }

    /**
     * stops client
     */
    override fun onClose() {
        httpClient?.cancel()
    }

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    suspend fun speechToText(data: List<Byte>): ServiceResponse<*> {
        val event = eventLogger.event(EventType.HttpClientSpeechToText)
        logger.v { "sending speechToText \nendpoint:\n${params.speechToTextHttpEndpoint}\ndata:\n${data.size}" }

        return httpClient?.let { client ->
            event.loading()
            try {

                val request = client.post(
                    url = Url("${params.speechToTextHttpEndpoint}?noheader=true")
                ) {
                    setBody(data.toByteArray())
                }

                val response = request.body<String>()
                logger.v { "speechToText received:\n$response" }

                event.success(response)
                return ServiceResponse.Success(response)
            } catch (e: Exception) {

                event.error(e)
                logger.e(e) { "sending speechToText Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            event.error("NotInitialized")
            return ServiceResponse.NotInitialized
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
    suspend fun recognizeIntent(text: String): ServiceResponse<*> {
        val event = eventLogger.event(EventType.RecognizeIntent)
        logger.v { "sending intentRecognition text\nendpoint:\n${params.intentRecognitionHttpEndpoint}\ntext:\n$text" }

        return httpClient?.let { client ->
            event.loading()
            try {
                logger.v { "intent will be handled directly ${params.isHandleIntentDirectly}" }

                val request = client.post(
                    url = Url(
                        "${params.intentRecognitionHttpEndpoint}${
                            if (!params.isHandleIntentDirectly) {
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
                val data = if (
                    Json.decodeFromString<JsonObject>(response)["intent"]?.jsonObject?.get("name")?.jsonPrimitive
                        .toString()
                        .isNotEmpty()
                ) {
                    //there was an intent found, return json
                    response
                } else {
                    //there was no intent found, return null
                    "NoIntentFound"
                }
                logger.v { "intentRecognition received:\n$data" }
                event.success(data)
                return ServiceResponse.Success(data)

            } catch (e: Exception) {

                event.error(e)
                logger.e(e) { "sending intentRecognition Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            event.error("NotInitialized")
            return ServiceResponse.NotInitialized
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
    suspend fun textToSpeech(text: String): ServiceResponse<*> {
        val event = eventLogger.event(EventType.HttpClientTextToSpeech)
        logger.v { "sending text to speech\nendpoint:\n${params.textToSpeechHttpEndpoint}\ntext:\n$text" }

        return httpClient?.let { client ->
            event.loading()
            try {
                val request = client.post(
                    url = Url(params.textToSpeechHttpEndpoint)
                ) {
                    setBody(text)
                }

                val response = request.body<ByteArray>().toList()
                logger.v { "textToSpeech received Data" }
                event.success(response.toString())
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                logger.e(e) { "sending text to speech Exception" }
                event.error(e)
                return ServiceResponse.Error(e)

            }
        } ?: run {
            event.error("NotInitialized")
            return ServiceResponse.NotInitialized
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>): ServiceResponse<*> {
        logger.v { "sending audio \nendpoint:\n${params.audioPlayingHttpEndpoint}data:\n${data.size}" }

        return httpClient?.let { client ->
            try {
                val request = client.post(
                    url = Url(params.audioPlayingHttpEndpoint)
                ) {
                    setAttributes {
                        contentType(ContentType("audio", "wav"))
                    }
                    setBody(data.toByteArray())
                }

                val response = request.body<String>()
                logger.v { "sending audio received:\n${response}" }
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                logger.e(e) { "sending audio Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            return ServiceResponse.NotInitialized
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
    suspend fun intentHandling(intent: String): ServiceResponse<*> {
        logger.v { "sending intentHandling\nendpoint:\n${params.intentHandlingHttpEndpoint}\nintent:\n$intent" }
        return httpClient?.let { client ->
            try {

                val request = client.post(
                    url = Url(params.intentHandlingHttpEndpoint)
                ) {
                    setBody(intent)
                }

                val response = request.body<String>()
                logger.v { "sending intent received:\n${response}" }
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                logger.e(e) { "sending intentHandling Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            return ServiceResponse.NotInitialized
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): ServiceResponse<*> {
        logger.v {
            "sending intent as Event to Home Assistant\nendpoint:\n" +
                    "${params.intentHandlingHassEndpoint}/api/events/rhasspy_$intentName\nintent:\n$json"
        }

        return httpClient?.let { client ->
            try {
                logger.v { "complete endpoint url" }

                val request = client.post(
                    url = Url("${params.intentHandlingHassEndpoint}/api/events/rhasspy_$intentName")
                ) {
                    buildHeaders {
                        header("Authorization", "Bearer ${params.intentHandlingHassAccessToken}")
                        contentType(ContentType("application", "json"))
                    }
                    setBody(json)
                }

                val response = request.body<String>()
                logger.v { "sending intent received:\n${response}" }
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                logger.e(e) { "sending hassEvent Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            return ServiceResponse.NotInitialized
        }
    }


    /**
     * send intent as Intent to Home Assistant
     */
    suspend fun hassIntent(intentJson: String): ServiceResponse<*> {
        logger.v {
            "sending intent as Intent to Home Assistant\nendpoint:\n" +
                    "${params.intentHandlingHassEndpoint}/api/intent/handle\nintent:\n$intentJson"
        }

        return httpClient?.let { client ->
            try {

                val request = client.post(
                    url = Url("${params.intentHandlingHassEndpoint}/api/intent/handle")
                ) {
                    buildHeaders {
                        header("Authorization", "Bearer ${params.intentHandlingHassAccessToken}")
                        contentType(ContentType("application", "json"))
                    }
                    setBody(intentJson)
                }

                val response = request.body<String>()
                logger.v { "sending intent received:\n${response}" }
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                logger.e(e) { "sending hassIntent Exception" }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            return ServiceResponse.NotInitialized
        }
    }

}