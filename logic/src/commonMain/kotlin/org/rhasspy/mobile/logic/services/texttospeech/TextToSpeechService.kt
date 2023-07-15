package org.rhasspy.mobile.logic.services.texttospeech

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayAudio
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.readOnly

interface ITextToSpeechService : IService {

    override val serviceState: StateFlow<ServiceState>

    suspend fun textToSpeech(sessionId: String, text: String)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class TextToSpeechService(
    paramsCreator: TextToSpeechServiceParamsCreator
) : ITextToSpeechService {

    override val logger = LogType.TextToSpeechService.logger()

    private val httpClientService by inject<IHttpClientService>()
    private val mqttClientService by inject<IMqttService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<TextToSpeechServiceParams> = paramsCreator()
    private val params: TextToSpeechServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when playing audio is finished
     */
    override suspend fun textToSpeech(sessionId: String, text: String) {
        logger.d { "textToSpeech sessionId: $sessionId text: $text" }
        when (params.textToSpeechOption) {
            TextToSpeechOption.RemoteHTTP -> {
                val result = httpClientService.textToSpeech(text)
                _serviceState.value = when (result) {
                    is HttpClientResult.Error -> ServiceState.Exception(result.exception)
                    is HttpClientResult.Success -> ServiceState.Success
                }
                val action = when (result) {
                    is HttpClientResult.Error -> AsrError(Source.HttpApi)
                    is HttpClientResult.Success -> PlayAudio(Source.HttpApi, result.data)
                }
                serviceMiddleware.action(action)
            }

            TextToSpeechOption.RemoteMQTT -> _serviceState.value = mqttClientService.say(sessionId, text)
            TextToSpeechOption.Disabled -> Unit
        }
    }

}