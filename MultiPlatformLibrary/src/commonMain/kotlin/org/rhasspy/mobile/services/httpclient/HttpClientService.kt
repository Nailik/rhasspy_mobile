package org.rhasspy.mobile.services.httpclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.cancel
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.nativeutils.configureEngine
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientServiceErrorType.*
import org.rhasspy.mobile.settings.option.IntentHandlingOption

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
class HttpClientService : IService() {
    private val logger = LogType.HttpClientService.logger()

    private val params by inject<HttpClientServiceParams>()

    private val audioContentType = ContentType("audio", "wav")
    private val jsonContentType = ContentType("application", "json")
    private fun HttpMessageBuilder.hassAuthorization() =
        this.header("Authorization", "Bearer ${params.intentHandlingHassAccessToken}")

    private val isHandleIntentDirectly =
        params.intentHandlingOption == IntentHandlingOption.WithRecognition

    private val speechToTextUrl =
        if (params.isUseCustomSpeechToTextHttpEndpoint) {
            params.speechToTextHttpEndpoint
        } else {
            HttpClientPath.SpeechToText.fromBaseConfiguration()
        } + "?noheader=true"

    private val recognizeIntentUrl =
        if (params.isUseCustomIntentRecognitionHttpEndpoint) {
            params.intentRecognitionHttpEndpoint
        } else {
            HttpClientPath.TextToIntent.fromBaseConfiguration()
        } + if (!isHandleIntentDirectly) {
            "?nohass=true"
        } else ""

    private val textToSpeechUrl = if (params.isUseCustomTextToSpeechHttpEndpoint) {
        params.textToSpeechHttpEndpoint
    } else {
        HttpClientPath.TextToSpeech.fromBaseConfiguration()
    }

    private val audioPlayingUrl = if (params.isUseCustomAudioPlayingEndpoint) {
        params.audioPlayingHttpEndpoint
    } else {
        HttpClientPath.PlayWav.fromBaseConfiguration()
    }

    private val hassEventUrl = "${params.intentHandlingHassEndpoint}/api/events/rhasspy_"
    private val hassIntentUrl = "${params.intentHandlingHassEndpoint}/api/intent/handle"

    private var httpClient: HttpClient? = null

    /**
     * starts client and updates event
     */
    init {
        logger.d { "initialize" }
        try {
            //starting
            httpClient = buildClient()
        } catch (exception: Exception) {
            //start error
            logger.e(exception) { "error on building client" }
        }
    }

    /**
     * stops client
     */
    override fun onClose() {
        logger.d { "onClose" }
        httpClient?.cancel()
    }

    /**
     * builds client
     */
    private fun buildClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = params.httpClientTimeout
            }
            engine {
                configureEngine(params.isHttpSSLVerificationDisabled)
            }
        }
    }


    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    suspend fun speechToText(data: List<Byte>): String? {
        logger.d { "speechToText dataSize: ${data.size}" }
        return post<String>(speechToTextUrl) {
            setBody(data.toByteArray())
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
    suspend fun recognizeIntent(text: String): String? {
        logger.d { "recognizeIntent text: $text" }
        return post<String>(recognizeIntentUrl) {
            setBody(text)
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
    suspend fun textToSpeech(text: String): ByteArray? {
        logger.d { "textToSpeech text: $text" }
        return post<ByteArray>(textToSpeechUrl) {
            setBody(text)
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>): String? {
        logger.d { "playWav dataSize: ${data.size}" }
        return post<String>(audioPlayingUrl) {
            setAttributes {
                contentType(audioContentType)
            }
            setBody(data.toByteArray())
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
    suspend fun intentHandling(intent: String): String? {
        logger.d { "intentHandling intent: $intent" }
        return post<String>(params.intentHandlingHttpEndpoint) {
            setBody(intent)
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): String? {
        logger.d { "hassEvent json: $json intentName: $intentName" }
        return post<String>("$hassEventUrl$intentName") {
            buildHeaders {
                hassAuthorization()
                contentType(jsonContentType)
            }
            setBody(json)
        }
    }


    /**
     * send intent as Intent to Home Assistant
     */
    suspend fun hassIntent(intentJson: String): String? {
        logger.d { "hassIntent json: $intentJson" }
        return post<String>(hassIntentUrl) {
            buildHeaders {
                hassAuthorization()
                contentType(jsonContentType)
            }
            setBody(intentJson)
        }
    }


    /**
     * post data to endpoint
     * handles even in event logger
     */
    private suspend inline fun <reified T> post(url: String, block: HttpRequestBuilder.() -> Unit): T? {
        return httpClient?.let { client ->
            try {
                val request = client.post(url, block)
                val result = request.body<T>()
                if (result is ByteArray) {
                    logger.d { "post result size: ${result.size}" }
                } else {
                    logger.d { "post result data: $result" }
                }
                return result
            } catch (exception: Exception) {
                logger.e(exception) { "post result error" }
                return null
            }
        } ?: run {
            logger.a { "post client not initialized" }
            return null
        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    private fun mapError(exception: Exception): HttpClientServiceErrorType? {
        return if (exception::class.simpleName == IllegalArgumentException.description) {
            if (exception.message == InvalidTLSRecordType.description) {
                InvalidTLSRecordType
            } else {
                IllegalArgumentException
            }
        } else if (exception::class.simpleName == UnresolvedAddressException.description) {
            UnresolvedAddressException
        } else if (exception::class.simpleName == ConnectException.description) {
            if (exception.message == ConnectionRefused.description) {
                ConnectionRefused
            } else {
                ConnectException
            }
            UnresolvedAddressException
        } else {
            null
        }
    }

}