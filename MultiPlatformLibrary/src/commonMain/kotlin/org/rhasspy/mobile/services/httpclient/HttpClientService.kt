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
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.rhasspy.mobile.ServiceTestName
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.nativeutils.configureEngine
import org.rhasspy.mobile.services.httpclient.data.HttpClientPath
import org.rhasspy.mobile.services.`interface`.IService2
import org.rhasspy.mobile.services.statemachine.StateMachineService
import org.rhasspy.mobile.settings.ConfigurationSettings

class HttpClientService(
    params: HttpClientParams = HttpClientService.loadParamsFromConfiguration(),
    isTest: Boolean = false
) : IService2<HttpClientParams>(
    params = params,
    isTest = isTest,
    serviceName = ServiceTestName.HttpClient
) {

    private val logger = Logger.withTag("HttpClientService")
    private var httpClient: HttpClient? = null
    private val stateMachineService by inject<StateMachineService>(named(if (isTest) ServiceTestName.StateMachineTest else ServiceTestName.StateMachine))

    companion object {
        fun loadParamsFromConfiguration(): HttpClientParams {
            return HttpClientParams(
                isHttpSSLVerificationDisabled = ConfigurationSettings.isHttpServerSSLEnabled.value,
                speechToTextHttpEndpoint = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
                    ConfigurationSettings.speechToTextHttpEndpoint.value
                } else {
                    "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.SpeechToText}"
                },
                intentRecognitionHttpEndpoint = if (ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value) {
                    ConfigurationSettings.intentRecognitionHttpEndpoint.value
                } else {
                    "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToIntent}"
                },
                isHandleIntentDirectly = ConfigurationSettings.intentHandlingOption.value == IntentHandlingOptions.WithRecognition,
                textToSpeechHttpEndpoint = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
                    ConfigurationSettings.speechToTextHttpEndpoint.value
                } else {
                    "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToSpeech}"
                },
                audioPlayingHttpEndpoint = ConfigurationSettings.audioPlayingHttpEndpoint.value,
                intentHandlingHttpEndpoint = ConfigurationSettings.intentHandlingHttpEndpoint.value,
                intentHandlingHassEndpoint = ConfigurationSettings.intentHandlingHassEndpoint.value,
                intentHandlingHassAccessToken = ConfigurationSettings.intentHandlingHassAccessToken.value
            )
        }

    }

    override fun onStart(scope: CoroutineScope) {
        //nothing to do
        scope.launch {
            httpClient = HttpClient(CIO) {
                expectSuccess = true
                install(WebSockets)
                install(HttpTimeout) {
                    requestTimeoutMillis = 10000
                }
                engine {
                    configureEngine(params.isHttpSSLVerificationDisabled)
                }
            }
        }
    }

    override fun onStop() {
        //nothing to do
        httpClient?.cancel()
    }

    override fun loadParamsFromConfiguration() = HttpClientService.loadParamsFromConfiguration()

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    suspend fun speechToText(data: List<Byte>): Boolean {
        logger.v { "sending speechToText \nendpoint:\n${params.speechToTextHttpEndpoint}\ndata:\n${data.size}" }

        return httpClient?.let { client ->
            return try {
                val request = client.post(
                    url = Url("${params.speechToTextHttpEndpoint}?noheader=true")
                ) {
                    setBody(data.toByteArray())
                }

                val response = request.body<String>()
                logger.v { "speechToText received:\n$response" }

                stateMachineService.speechToTextResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending speechToText Exception" }
                false
            }
        } ?: run {
            false
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
    suspend fun recognizeIntent(text: String): Boolean {
        logger.v { "sending intentRecognition text\nendpoint:\n${params.intentRecognitionHttpEndpoint}\ntext:\n$text" }

        return httpClient?.let { client ->
            return try {
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
                val data =
                    if (Json.decodeFromString<JsonObject>(response)["intent"]?.jsonObject?.get("name")?.jsonPrimitive.toString().isNotEmpty()) {
                        //there was an intent found, return json
                        response
                    } else {
                        //there was no intent found, return null
                        null
                    }
                logger.v { "intentRecognition received:\n$data" }
                stateMachineService.recognizeIntentResponseHttp(data)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending intentRecognition Exception" }
                false
            }
        } ?: run {
            false
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
    suspend fun textToSpeech(text: String): Boolean {
        logger.v { "sending text to speech\nendpoint:\n${params.textToSpeechHttpEndpoint}\ntext:\n$text" }

        return httpClient?.let { client ->
            return try {
                val request = client.post(
                    url = Url(params.textToSpeechHttpEndpoint)
                ) {
                    setBody(text)
                }

                val response = request.body<ByteArray>().toList()

                logger.v { "textToSpeech received Data" }
                stateMachineService.textToSpeechResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending text to speech Exception" }
                false
            }
        } ?: run {
            false
        }
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>): Boolean {
        logger.v { "sending audio \nendpoint:\n${params.audioPlayingHttpEndpoint}data:\n${data.size}" }

        return httpClient?.let { client ->
            return try {
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
                stateMachineService.playWavResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending audio Exception" }
                false
            }
        } ?: run {
            false
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
    suspend fun intentHandling(intent: String): Boolean {
        logger.v { "sending intentHandling\nendpoint:\n${params.intentHandlingHttpEndpoint}\nintent:\n$intent" }
        return httpClient?.let { client ->
            return try {

                val request = client.post(
                    url = Url(params.intentHandlingHttpEndpoint)
                ) {
                    setBody(intent)
                }

                val response = request.body<String>()
                logger.v { "sending intent received:\n${response}" }
                stateMachineService.intentHandlingResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending intentHandling Exception" }
                false
            }
        } ?: run {
            false
        }
    }

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String): Boolean {
        logger.v {
            "sending intent as Event to Home Assistant\nendpoint:\n" +
                    "${params.intentHandlingHassEndpoint}/api/events/rhasspy_$intentName\nintent:\n$json"
        }

        return httpClient?.let { client ->
            return try {
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
                stateMachineService.hassEventResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending hassEvent Exception" }
                false
            }

        } ?: run {
            false
        }
    }


    /**
     * send intent as Intent to Home Assistant
     */
    suspend fun hassIntent(intentJson: String): Boolean {
        logger.v {
            "sending intent as Intent to Home Assistant\nendpoint:\n" +
                    "${params.intentHandlingHassEndpoint}/api/intent/handle\nintent:\n$intentJson"
        }

        return httpClient?.let { client ->
            return try {

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
                stateMachineService.hassIntentResponseHttp(response)
                true
            } catch (e: Exception) {

                logger.e(e) { "sending hassIntent Exception" }
                false
            }
        } ?: run {
            false
        }
    }

}