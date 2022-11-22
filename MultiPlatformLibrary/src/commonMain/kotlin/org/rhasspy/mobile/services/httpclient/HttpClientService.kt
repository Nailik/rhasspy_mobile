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
import org.koin.core.qualifier.named
import org.rhasspy.mobile.data.IntentHandlingOptions
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

    enum class ErrorType(val description: String) {
        NotInitialized(""),
        IllegalArgumentException("IllegalArgumentException"),
        InvalidTLSRecordType("Invalid TLS record type code: 72"), // Invalid TLS record type code: 72)
        UnresolvedAddressException("UnresolvedAddressException"), //server cannot be reached
        ConnectException("ConnectException"),
        ConnectionRefused("Connection refused") //wrong port or address
    }

    private val params by inject<HttpClientServiceParams>()

    private val eventLogger by inject<EventLogger>(named(EventTag.HttpClientService.name))

    private val audioContentType = ContentType("audio", "wav")
    private val jsonContentType = ContentType("application", "json")
    private fun HttpMessageBuilder.hassAuthorization() = this.header("Authorization", "Bearer ${params.intentHandlingHassAccessToken}")

    private val isHandleIntentDirectly = params.intentHandlingOption == IntentHandlingOptions.WithRecognition

    private val speechToTextUrl =
        (if (params.isUseCustomSpeechToTextHttpEndpoint) params.speechToTextHttpEndpoint else params.httpServerEndpoint) +
                "${HttpClientPath.SpeechToText.path}?noheader=true"
    private val recognizeIntentUrl =
        (if (params.isUseCustomIntentRecognitionHttpEndpoint) params.intentRecognitionHttpEndpoint else params.httpServerEndpoint) +
                HttpClientPath.TextToIntent.path +
                if (!isHandleIntentDirectly) {
                    "?nohass=true"
                } else ""
    private val textToSpeechUrl =
        (if (params.isUseCustomTextToSpeechHttpEndpoint) params.textToSpeechHttpEndpoint else params.httpServerEndpoint) +
                HttpClientPath.TextToSpeech.path
    private val hassEventUrl = "${params.intentHandlingHassEndpoint}/api/events/rhasspy_"
    private val hassIntentUrl = "${params.intentHandlingHassEndpoint}/api/intent/handle"

    private var httpClient: HttpClient? = null

    /**
     * starts client and updates event
     */
    init {
        val startEvent = eventLogger.event(EventType.HttpClientStart)

        //starting
        startEvent.loading()
        try {
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
        return post<String>(EventType.HttpClientSpeechToText, speechToTextUrl) {
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
        return post<String>(EventType.HttpClientRecognizeIntent, recognizeIntentUrl) {
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
        return post<ByteArray>(EventType.HttpClientTextToSpeech, textToSpeechUrl) {
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
        return post<String>(EventType.HttpClientPlayWav, params.audioPlayingHttpEndpoint) {
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
        return post<String>(EventType.HttpClientIntentHandling, params.intentHandlingHttpEndpoint) {
            setBody(intent)
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): ServiceResponse<*> {
        return post<String>(EventType.HttpClientHassEvent, "$hassEventUrl$intentName") {
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
        return post<String>(EventType.HttpClientHassIntent, hassIntentUrl) {
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
        val event = eventLogger.event(eventType)

        return httpClient?.let { client ->
            event.loading()
            try {

                val request = client.post(url, block)

                val response = request.body<T>()
                event.success(request.status.toString())
                return ServiceResponse.Success(response)

            } catch (e: Exception) {

                getErrorDescription(e)?.also { description ->
                    event.error(description)
                } ?: run {
                    event.error(e)
                }
                return ServiceResponse.Error(e)

            }
        } ?: run {
            event.error(ErrorType.NotInitialized.toString())
            return ServiceResponse.NotInitialized
        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    private fun getErrorDescription(e: Exception): String? {
        return if (e::class.simpleName == ErrorType.IllegalArgumentException.description) {
            if (e.message == ErrorType.InvalidTLSRecordType.description) {
                ErrorType.InvalidTLSRecordType.toString()
            } else {
                ErrorType.IllegalArgumentException.toString()
            }
        } else if (e::class.simpleName == ErrorType.UnresolvedAddressException.description) {
            ErrorType.UnresolvedAddressException.toString()
        } else if (e::class.simpleName == ErrorType.ConnectException.description) {
            if (e.message == ErrorType.ConnectionRefused.description) {
                ErrorType.ConnectionRefused.toString()
            } else {
                ErrorType.ConnectException.toString()
            }
            ErrorType.UnresolvedAddressException.toString()
        } else {
            null
        }
    }

}