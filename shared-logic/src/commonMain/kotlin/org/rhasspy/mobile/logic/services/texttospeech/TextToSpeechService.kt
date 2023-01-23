package org.rhasspy.mobile.logic.services.texttospeech

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.Action.DialogAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceState
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.settings.option.TextToSpeechOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class TextToSpeechService : IService() {
    private val logger = LogType.TextToSpeechService.logger()

    private val params by inject<TextToSpeechServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val serviceMiddleware by inject<ServiceMiddleware>()

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
                val result = httpClientService.textToSpeech(text)
                _serviceState.value = when (result) {
                    is HttpClientResult.Error -> ServiceState.Exception(result.exception)
                    is HttpClientResult.Success -> ServiceState.Success
                }
                val action = when (result) {
                    is HttpClientResult.Error -> DialogAction.AsrError(Source.HttpApi)
                    is HttpClientResult.Success -> DialogAction.PlayAudio(
                        Source.HttpApi,
                        result.data
                    )
                }
                serviceMiddleware.action(action)
            }

            TextToSpeechOption.RemoteMQTT -> _serviceState.value =
                mqttClientService.say(sessionId, text)

            TextToSpeechOption.Disabled -> {}
        }
    }

}