package org.rhasspy.mobile.logic.connections.httpclient

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
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.connections.httpclient.HttpClientResult.Error
import org.rhasspy.mobile.logic.connections.httpclient.HttpClientResult.KnownError
import org.rhasspy.mobile.logic.domains.speechtotext.StreamContent
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.*
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.ktor.configureEngine
import org.rhasspy.mobile.settings.repositories.IHttpConnectionSettingRepository

interface IHttpClientConnection : KoinComponent {

    fun speechToText(audioFilePath: Path, onResult: (result: HttpClientResult<String>) -> Unit)
    fun recognizeIntent(text: String, onResult: (result: HttpClientResult<String>) -> Unit)
    fun textToSpeech(text: String, volume: Float?, siteId: String?, onResult: (result: HttpClientResult<ByteArray>) -> Unit)
    fun playWav(audioSource: AudioSource, onResult: (result: HttpClientResult<String>) -> Unit)
    fun intentHandling(intent: String, onResult: (result: HttpClientResult<String>) -> Unit)
    fun homeAssistantEvent(json: String, intentName: String, onResult: (result: HttpClientResult<String>) -> Unit)
    fun homeAssistantIntent(intentJson: String, onResult: (result: HttpClientResult<String>) -> Unit)

}

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class HttpClientConnection(
    private val connectionId: StateFlow<Long?>
) : IHttpClientConnection {

    private val logger = LogType.HttpClientService.logger()

    private val nativeApplication by inject<NativeApplication>()
    private val httpConnectionSettingRepository by inject<IHttpConnectionSettingRepository>()

    private var httpConnectionParams: HttpConnectionParams? = null

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private val audioContentType = ContentType("audio", "wav")
    private val jsonContentType = ContentType("application", "json")
    private fun HttpMessageBuilder.authorization(bearerToken: String?) = bearerToken?.let { this.header("Authorization", "Bearer $bearerToken") } ?: this

    private var httpClient: HttpClient? = null

    init {
        coroutineScope.launch {
            connectionId.collect { id ->
                if (id != null) collectParams(id)
            }
        }
    }

    private suspend fun collectParams(connectionId: Long) {
        httpConnectionSettingRepository.getHttpConnection(connectionId).collect {
            httpConnectionParams = it
            httpClient?.cancel()

            try {
                httpClient = buildClient(it)
            } catch (exception: Exception) {
                logger.e(exception) { "error on building client" }
                //TODO send exception somewhere
            }
        }
    }

    fun close() {
        coroutineScope.cancel()
        httpClient?.close()
    }

    /**
     * builds client
     */
    private fun buildClient(params: HttpConnectionParams): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = params.timeout
            }
            engine {
                configureEngine(params.isSSLVerificationDisabled)
            }
        }
    }


    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    override fun speechToText(audioFilePath: Path, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
        logger.d { "speechToText: audioFilePath.name" }

            post(
                url = "$host/api/speech-to-text",
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                    }
                    setBody(StreamContent(audioFilePath))
                },
                onResult = onResult
            )
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
    override fun recognizeIntent(text: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
        logger.d { "recognizeIntent text: $text" }

            post(
                url = "$host/api/text-to-intent",
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                    }
                    setBody(text)
                },
                onResult = onResult
            )
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
    override fun textToSpeech(text: String, volume: Float?, siteId: String?, onResult: (result: HttpClientResult<ByteArray>) -> Unit) {
        httpConnectionParams?.apply {
        logger.d { "textToSpeech text: $text" }

            post(
                url = "$host/api/text-to-speech/${volume?.let { "?volume=$it" } ?: ""}${siteId?.let { "?siteId=$it" } ?: ""}",
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                    }
                    setBody(text)
                },
                onResult = onResult
            )
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun playWav(audioSource: AudioSource, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
            logger.d { "playWav size: $audioSource" }
            @Suppress("DEPRECATION")
            val body = when (audioSource) {
                is Data -> audioSource.data
                is File -> StreamContent(audioSource.path)
                is Resource -> audioSource.fileResource.commonData(nativeApplication)
            }
            return post(
                url = "$host/api/play-wav",
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                        contentType(audioContentType)
                    }
                    setBody(body)
                },
                onResult = onResult
            )
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
    override fun intentHandling(intent: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
            logger.d { "intentHandling intent: $intent" }
            post(
                url = host,
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                    }
                    setBody(intent)
                },
                onResult = onResult
            )
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    override fun homeAssistantEvent(json: String, intentName: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
            logger.d { "homeAssistantEvent json: $json intentName: $intentName" }
            post(
                url = "$host/api/events/rhasspy_$intentName",
                block = {
                    buildHeaders {
                        authorization(bearerToken)
                        contentType(jsonContentType)
                    }
                    setBody(json)
                },
                onResult = onResult
            )
        }
    }


    /**
     * send intent as Intent to Home Assistant
     */
    override fun homeAssistantIntent(intentJson: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        httpConnectionParams?.apply {
            logger.d { "homeAssistantIntent json: $intentJson" }
            post(
                url = "$host/api/intent/handle", block = {
                    buildHeaders {
                        authorization(bearerToken)
                        contentType(jsonContentType)
                    }
                    setBody(intentJson)
                },
                onResult = onResult
            )
        }
    }


    /**
     * post data to endpoint
     * handles even in event logger
     */
    private inline fun <reified T> post(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit,
        crossinline onResult: (result: HttpClientResult<T>) -> Unit
    ) {
        coroutineScope.launch {
            val result = httpClient?.let { client ->
                try {
                    val request = client.post(url, block)
                    val result = request.body<T>()
                    if (result is ByteArray) {
                        logger.d { "post result size: ${result.size}" }
                    } else {
                        logger.d { "post result data: $result" }
                    }

                    HttpClientResult.Success(result)

                } catch (exception: Exception) {

                    logger.e(exception) { "post result error" }
                    mapError(exception)

                }
            } ?: run {
                logger.a { "post client not initialized" }
                Error(Exception())
            }

            onResult(result)
        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    private fun <T> mapError(exception: Exception): HttpClientResult<T> {
        val type = if (exception::class.simpleName == "IllegalArgumentException") {
            if (exception.message == "Invalid TLS record type code: 72") {
                HttpClientErrorType.InvalidTLSRecordType
            } else {
                HttpClientErrorType.IllegalArgumentError
            }
        } else if (exception::class.simpleName == "UnresolvedAddressException") {
            HttpClientErrorType.UnresolvedAddressError
        } else if (exception::class.simpleName == "ConnectException") {
            if (exception.message == "Connection refused") {
                HttpClientErrorType.ConnectionRefused
            } else {
                HttpClientErrorType.ConnectError
            }
            HttpClientErrorType.UnresolvedAddressError
        } else {
            null
        }

        return if (type == null) Error<T>(exception) else KnownError<T>(type)
    }

}