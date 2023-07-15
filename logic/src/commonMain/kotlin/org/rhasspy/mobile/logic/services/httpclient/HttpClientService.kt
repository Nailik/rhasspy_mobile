package org.rhasspy.mobile.logic.services.httpclient

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.buildHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.speechtotext.StreamContent
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.*
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.ktor.configureEngine
import org.rhasspy.mobile.platformspecific.readOnly

interface IHttpClientService : IService {

    override val serviceState: StateFlow<ServiceState>

    suspend fun speechToText(audioFilePath: Path): HttpClientResult<String>
    suspend fun recognizeIntent(text: String): HttpClientResult<String>
    suspend fun textToSpeech(text: String): HttpClientResult<ByteArray>
    suspend fun playWav(audioSource: AudioSource): HttpClientResult<String>
    suspend fun intentHandling(intent: String): HttpClientResult<String>
    suspend fun homeAssistantEvent(json: String, intentName: String): HttpClientResult<String>
    suspend fun homeAssistantIntent(intentJson: String): HttpClientResult<String>
}

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class HttpClientService(
    paramsCreator: HttpClientServiceParamsCreator
) : IHttpClientService {

    override val logger = LogType.HttpClientService.logger()

    private val nativeApplication by inject<NativeApplication>()

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val paramsFlow: StateFlow<HttpClientServiceParams> = paramsCreator()
    private val params get() = paramsFlow.value

    private val audioContentType = ContentType("audio", "wav")
    private val jsonContentType = ContentType("application", "json")
    private fun HttpMessageBuilder.hassAuthorization() =
        this.header("Authorization", "Bearer ${params.intentHandlingHomeAssistantAccessToken}")

    private val isHandleIntentDirectly
        get() =
            params.intentHandlingOption == IntentHandlingOption.WithRecognition

    private val speechToTextUrl
        get() =
            if (params.isUseCustomSpeechToTextHttpEndpoint) {
                params.speechToTextHttpEndpoint
            } else {
                "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/${HttpClientPath.SpeechToText.path}"
            } + "?noheader=true"

    private val recognizeIntentUrl
        get() =
            if (params.isUseCustomIntentRecognitionHttpEndpoint) {
                params.intentRecognitionHttpEndpoint
            } else {
                "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/${HttpClientPath.TextToIntent.path}"
            } + if (!isHandleIntentDirectly) {
                "?nohass=true"
            } else ""

    private val textToSpeechUrl
        get() = if (params.isUseCustomTextToSpeechHttpEndpoint) {
            params.textToSpeechHttpEndpoint
        } else {
            "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/${HttpClientPath.TextToSpeech.path}"
        }

    private val audioPlayingUrl
        get() = if (params.isUseCustomAudioPlayingEndpoint) {
            params.audioPlayingHttpEndpoint
        } else {
            "${params.httpClientServerEndpointHost}:${params.httpClientServerEndpointPort}/${HttpClientPath.PlayWav.path}"
        }

    private val hassEventUrl get() = "${params.intentHandlingHomeAssistantEndpoint}/api/events/rhasspy_"
    private val hassIntentUrl get() = "${params.intentHandlingHomeAssistantEndpoint}/api/intent/handle"

    private var httpClient: HttpClient? = null

    /**
     * starts client and updates event
     */
    init {
        coroutineScope.launch {
            paramsFlow.collect {
                stop()
                start()
            }
        }
    }

    /**
     * starts client
     */
    private fun start() {
        logger.d { "initialize" }
        _serviceState.value = ServiceState.Loading

        try {
            //starting
            httpClient = buildClient()
            _serviceState.value = Success
        } catch (exception: Exception) {
            //start error
            logger.e(exception) { "error on building client" }
            _serviceState.value = ServiceState.Exception(exception)
        }
    }

    /**
     * stops client
     */
    private fun stop() {
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
    override suspend fun speechToText(audioFilePath: Path): HttpClientResult<String> {
        logger.d { "speechToText: audioFilePath.name" }

        return post(speechToTextUrl) {
            setBody(StreamContent(audioFilePath))
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
    override suspend fun recognizeIntent(text: String): HttpClientResult<String> {
        logger.d { "recognizeIntent text: $text" }
        return post(recognizeIntentUrl) {
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
    override suspend fun textToSpeech(text: String): HttpClientResult<ByteArray> {
        logger.d { "textToSpeech text: $text" }
        return post(textToSpeechUrl) {
            setBody(text)
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override suspend fun playWav(audioSource: AudioSource): HttpClientResult<String> {
        logger.d { "playWav size: $audioSource" }
        @Suppress("DEPRECATION")
        val body = when (audioSource) {
            is Data     -> audioSource.data
            is File     -> StreamContent(audioSource.path)
            is Resource -> audioSource.fileResource.commonData(nativeApplication)
        }
        return post(audioPlayingUrl) {
            setAttributes {
                contentType(audioContentType)
            }
            setBody(body)
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
    override suspend fun intentHandling(intent: String): HttpClientResult<String> {
        logger.d { "intentHandling intent: $intent" }
        return post(params.intentHandlingHttpEndpoint) {
            setBody(intent)
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    override suspend fun homeAssistantEvent(json: String, intentName: String): HttpClientResult<String> {
        logger.d { "homeAssistantEvent json: $json intentName: $intentName" }
        return post("$hassEventUrl$intentName") {
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
    override suspend fun homeAssistantIntent(intentJson: String): HttpClientResult<String> {
        logger.d { "homeAssistantIntent json: $intentJson" }
        return post(hassIntentUrl) {
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
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpClientResult<T> {
        return httpClient?.let { client ->
            try {
                val request = client.post(url, block)
                val result = request.body<T>()
                if (result is ByteArray) {
                    logger.d { "post result size: ${result.size}" }
                } else {
                    logger.d { "post result data: $result" }
                }

                _serviceState.value = Success
                HttpClientResult.Success(result)

            } catch (exception: Exception) {

                logger.e(exception) { "post result error" }
                _serviceState.value = mapError(exception)
                HttpClientResult.Error(exception)

            }
        } ?: run {

            logger.a { "post client not initialized" }
            _serviceState.value = ServiceState.Exception()
            HttpClientResult.Error(Exception())

        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    private fun mapError(exception: Exception): ServiceState {
        val type = if (exception::class.simpleName == "IllegalArgumentException") {
            if (exception.message == "Invalid TLS record type code: 72") {
                HttpClientServiceStateType.InvalidTLSRecordType
            } else {
                HttpClientServiceStateType.IllegalArgumentException
            }
        } else if (exception::class.simpleName == "UnresolvedAddressException") {
            HttpClientServiceStateType.UnresolvedAddressException
        } else if (exception::class.simpleName == "ConnectException") {
            if (exception.message == "Connection refused") {
                HttpClientServiceStateType.ConnectionRefused
            } else {
                HttpClientServiceStateType.ConnectException
            }
            HttpClientServiceStateType.UnresolvedAddressException
        } else {
            null
        }

        return type?.serviceState ?: ServiceState.Exception(exception)
    }

}