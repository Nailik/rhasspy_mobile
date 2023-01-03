package org.rhasspy.mobile.services.texttospeech

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.option.TextToSpeechOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class TextToSpeechService : IService() {
    private val logger = LogType.TextToSpeechService.logger()

    private val params by inject<TextToSpeechServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success())
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when playing audio is finished
     */
    suspend fun textToSpeech(sessionId: String, text: String) {
        logger.d { "textToSpeech sessionId: $sessionId text: $text" }
        when (params.textToSpeechOption) {
            TextToSpeechOption.RemoteHTTP -> {
                httpClientService.textToSpeech(text)?.also {
                    serviceMiddleware.action(DialogAction.PlayAudio(Source.HttpApi, it))
                }
            }
            TextToSpeechOption.RemoteMQTT -> mqttClientService.say(sessionId, text)
            TextToSpeechOption.Disabled -> {}
        }
    }

}