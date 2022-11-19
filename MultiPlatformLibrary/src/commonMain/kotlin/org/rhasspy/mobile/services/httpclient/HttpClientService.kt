package org.rhasspy.mobile.services.httpclient

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.IServiceLink
import org.rhasspy.mobile.services.ServiceError
import org.rhasspy.mobile.services.httpclient.data.HttpClientCallType
import org.rhasspy.mobile.services.httpclient.data.HttpClientPath
import org.rhasspy.mobile.settings.ConfigurationSettings

class HttpClientService : IService<HttpClientCallType>() {

    private val logger = Logger.withTag("HttpClientService")

    private val _currentError = MutableSharedFlow<ServiceError<HttpClientCallType>?>()
    override val currentError = _currentError.readOnly

    private var httpClientLink: HttpClientLink? = null
    private var scope = CoroutineScope(Dispatchers.Default)

    override fun onStart(scope: CoroutineScope): IServiceLink {

        return HttpClientLink(
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
            intentHandlingHassAccessToken = ConfigurationSettings.intentHandlingHassAccessToken.value,
        )
    }

    override fun onStop() {
        scope.cancel()
    }

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    fun speechToText(data: List<Byte>) {
        scope.launch {
            httpClientLink?.speechToText(data)
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
    suspend fun intentRecognition(text: String) = httpClientLink?.intentRecognition(text)

    /**
     * api/text-to-speech
     * POST text and have Rhasspy speak it
     * ?voice=<voice> - override default TTS voice
     * ?language=<language> - override default TTS language or locale
     * ?repeat=true - have Rhasspy repeat the last sentence it spoke
     * ?volume=<volume> - volume level to speak at (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun textToSpeech(text: String) = httpClientLink?.textToSpeech(text)

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    suspend fun playWav(data: List<Byte>) = httpClientLink?.playWav(data)

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
    suspend fun intentHandling(intent: String) = httpClientLink?.intentHandling(intent)

    /**
     * send intent as Event to Home Assistant
     */
    suspend fun hassEvent(json: String, intentName: String) = httpClientLink?.hassEvent(json, intentName)

    /**
     * send intent as Intent to Home Assistant
     */
    suspend fun hassIntent(intent: String) = httpClientLink?.hassIntent(intent)

}