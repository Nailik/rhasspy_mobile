package org.rhasspy.mobile.logic.domains.texttospeech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.connections.httpclient.IHttpClientConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayAudio
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.readOnly

interface ITextToSpeechService : IService {

    override val serviceState: StateFlow<ServiceState>

    fun textToSpeech(text: String, volume: Float?, siteId: String, sessionId: String?)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class TextToSpeechService(
    paramsCreator: TextToSpeechServiceParamsCreator
) : ITextToSpeechService {

    private val logger = LogType.TextToSpeechService.logger()


    private val paramsFlow: StateFlow<TextToSpeechServiceParams> = paramsCreator()
    private val params: TextToSpeechServiceParams get() = paramsFlow.value

    private val mqttClientService by inject<IMqttService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val httpClientConnection by inject<IHttpClientConnection>()

    private val _serviceState = MutableStateFlow<ServiceState>(Success)
    override val serviceState = _serviceState.readOnly

    private val scope = CoroutineScope(Dispatchers.IO)
    private val mqttSessionId = "ITextToSpeechService"

    init {
        scope.launch {
            paramsFlow.collect {
                setupState()
            }
        }
    }

    private fun setupState() {
        _serviceState.value = when (params.textToSpeechOption) {
            TextToSpeechOption.RemoteHTTP -> Success
            TextToSpeechOption.RemoteMQTT -> Success
            TextToSpeechOption.Disabled   -> Disabled
        }
    }


    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when playing audio is finished
     */
    override fun textToSpeech(text: String, volume: Float?, siteId: String, sessionId: String?) {
        if (siteId != params.siteId) return

        logger.d { "textToSpeech sessionId: $sessionId text: $text" }
        when (params.textToSpeechOption) {
            TextToSpeechOption.RemoteHTTP -> {
                httpClientConnection.textToSpeech(text, volume, null) { result ->
                    _serviceState.value = when (result) {
                        is HttpClientResult.Error      -> result.toServiceState()
                        is HttpClientResult.Success    -> Success
                        is HttpClientResult.KnownError -> result.toServiceState()
                    }
                    val action = when (result) {
                        is HttpClientResult.Error      -> AsrError(Source.Local)
                        is HttpClientResult.Success    -> PlayAudio(Source.Local, result.data)
                        is HttpClientResult.KnownError -> AsrError(Source.Local)
                    }
                    serviceMiddleware.action(action)
                }
            }

            TextToSpeechOption.RemoteMQTT -> {
                if (sessionId == mqttSessionId) return
                mqttClientService.say(mqttSessionId, text, siteId) { _serviceState.value = it }
            }

            TextToSpeechOption.Disabled   -> _serviceState.value = Disabled
        }
    }

}