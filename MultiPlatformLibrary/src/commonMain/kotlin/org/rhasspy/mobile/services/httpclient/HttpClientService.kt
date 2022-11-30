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
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.middleware.ErrorType.HttpClientServiceErrorType
import org.rhasspy.mobile.middleware.ErrorType.HttpClientServiceErrorType.*
import org.rhasspy.mobile.middleware.EventType
import org.rhasspy.mobile.middleware.EventType.HttpClientServiceEventType.*
import org.rhasspy.mobile.middleware.IServiceMiddleware
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

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val audioContentType = ContentType("audio", "wav")
    private val jsonContentType = ContentType("application", "json")
    private fun HttpMessageBuilder.hassAuthorization() = this.header("Authorization", "Bearer ${params.intentHandlingHassAccessToken}")

    private val isHandleIntentDirectly = params.intentHandlingOption == IntentHandlingOptions.WithRecognition

    private val speechToTextUrl =
        if (params.isUseCustomSpeechToTextHttpEndpoint) {
            params.speechToTextHttpEndpoint
        } else {
            params.httpServerEndpoint + HttpClientPath.SpeechToText.path
        } + "?noheader=true"

    private val recognizeIntentUrl =
        if (params.isUseCustomIntentRecognitionHttpEndpoint) {
            params.intentRecognitionHttpEndpoint
        } else {
            params.httpServerEndpoint + HttpClientPath.TextToIntent.path

        } + if (!isHandleIntentDirectly) {
            "?nohass=true"
        } else ""

    private val textToSpeechUrl = if (params.isUseCustomTextToSpeechHttpEndpoint) {
        params.textToSpeechHttpEndpoint
    } else {
        params.httpServerEndpoint + HttpClientPath.TextToSpeech.path
    }

    private val audioPlayingUrl = if (params.isUseCustomAudioPlayingEndpoint) {
        params.audioPlayingHttpEndpoint
    } else {
        params.httpServerEndpoint + HttpClientPath.PlayWav.path
    }

    private val hassEventUrl = "${params.intentHandlingHassEndpoint}/api/events/rhasspy_"
    private val hassIntentUrl = "${params.intentHandlingHassEndpoint}/api/intent/handle"

    private var httpClient: HttpClient? = null

    /**
     * starts client and updates event
     */
    init {
        val startEvent = serviceMiddleware.createEvent(Start)

        try {
            //starting
            httpClient = buildClient()
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
     * builds client
     */
    private fun buildClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
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
    suspend fun speechToText(data: List<Byte>): ServiceResponse<*> {
        return post<String>(EventType.HttpClientServiceEventType.SpeechToText, speechToTextUrl) {
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
    suspend fun recognizeIntent(text: String): ServiceResponse<*> {
        return post<String>(RecognizeIntent, recognizeIntentUrl) {
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
    suspend fun textToSpeech(text: String): ServiceResponse<*> {
        return post<ByteArray>(TextToSpeech, textToSpeechUrl) {
            setBody(text)
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>): ServiceResponse<*> {
        return post<String>(PlayWav, audioPlayingUrl) {
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
    suspend fun intentHandling(intent: String): ServiceResponse<*> {
        return post<String>(IntentHandling, params.intentHandlingHttpEndpoint) {
            setBody(intent)
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): ServiceResponse<*> {
        return post<String>(HassEvent, "$hassEventUrl$intentName") {
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
    suspend fun hassIntent(intentJson: String): ServiceResponse<*> {
        return post<String>(HassIntent, hassIntentUrl) {
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
    private suspend inline fun <reified T> post(
        eventType: EventType,
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): ServiceResponse<*> {
        val postEvent = serviceMiddleware.createEvent(eventType)

        return httpClient?.let { client ->
            postEvent.loading()
            try {

                val request = client.post(url, block)

                val response = request.body<T>()

                postEvent.success()
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                mapError(e)?.also { description ->
                    postEvent.error(description)
                } ?: run {
                    postEvent.error(e)
                }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            postEvent.error(NotInitialized)
            return ServiceResponse.NotInitialized
        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    private fun mapError(e: Exception): HttpClientServiceErrorType? {
        return if (e::class.simpleName == IllegalArgumentException.description) {
            if (e.message == InvalidTLSRecordType.description) {
                InvalidTLSRecordType
            } else {
                IllegalArgumentException
            }
        } else if (e::class.simpleName == UnresolvedAddressException.description) {
            UnresolvedAddressException
        } else if (e::class.simpleName == ConnectException.description) {
            if (e.message == ConnectionRefused.description) {
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