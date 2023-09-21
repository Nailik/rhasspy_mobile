package org.rhasspy.mobile.logic.domains.tts

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.SynthesizeEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.TtsErrorEvent
import org.rhasspy.mobile.settings.ConfigurationSetting

interface ITtsDomain : IService {

    fun onSynthesize(synthesizeEvent: SynthesizeEvent, sessionId: String)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class TtsDomain(
    private val pipeline: IPipeline,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection
) : ITtsDomain {

    private val logger = Logger.withTag("TextToSpeechService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)
    private val params get() = ConfigurationSetting.ttsDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mqttSessionId = "ITextToSpeechService"

    init {
        scope.launch {
            ConfigurationSetting.ttsDomainData.data.collectLatest {
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.option) {
            TextToSpeechOption.Rhasspy2HermesHttp -> Success
            TextToSpeechOption.Rhasspy2HermesMQTT -> Success
            TextToSpeechOption.Disabled           -> Disabled
        }
    }

    //TODO do not call if local siteId is same as siteId inside Synthesize
    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when playing audio is finished
     */
    override fun onSynthesize(synthesizeEvent: SynthesizeEvent, sessionId: String) {
        logger.d { "textToSpeech sessionId: $synthesizeEvent" }
        serviceState.value = when (params.option) {
            TextToSpeechOption.Rhasspy2HermesHttp -> {
                rhasspy2HermesConnection.textToSpeech(synthesizeEvent.text, synthesizeEvent.volume, synthesizeEvent.siteId) { result ->
                    serviceState.value = result.toServiceState()

                    val event = when (result) {
                        is HttpClientResult.HttpClientError -> TtsErrorEvent
                        is HttpClientResult.Success         -> {
                            TtsErrorEvent
                            //TODO audio start event (read header from wav data) and then audio event and then audio end event
                            //TtsResultEvent(result.data)
                        }
                    }
                    pipeline.onEvent(event)
                }
                Loading
            }

            TextToSpeechOption.Rhasspy2HermesMQTT -> {
                if (sessionId == mqttSessionId) return
                mqttConnection.say(mqttSessionId, synthesizeEvent.text, synthesizeEvent.siteId) {
                    serviceState.value = it
                }
                Loading
            }

            TextToSpeechOption.Disabled           -> Disabled
        }
    }

}