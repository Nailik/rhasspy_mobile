package org.rhasspy.mobile.logic.domains.tts

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.TtsDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayBytes
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult.Error
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.snd.SndAudio.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.TtsResult
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.logic.pipeline.TtsResult.NotSynthesized

interface ITtsDomain : IService {

    suspend fun onSynthesize(
        sessionId: String,
        volume: Float?,
        siteId: String,
        handle: Handle,
        ): TtsResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class TtsDomain(
    private val params: TtsDomainData,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection
) : ITtsDomain {

    private val logger = Logger.withTag("TextToSpeechService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    init {
        serviceState.value = when (params.option) {
            TextToSpeechOption.Rhasspy2HermesHttp -> Success
            TextToSpeechOption.Rhasspy2HermesMQTT -> Success
            TextToSpeechOption.Disabled           -> Disabled
        }
    }

    override suspend fun onSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult {
        return when (params.option) {
            TextToSpeechOption.Rhasspy2HermesHttp ->
                onRhasspy2HermesHttpSynthesize(
                    volume = volume,
                    siteId = siteId,
                    handle = handle,
                )

            TextToSpeechOption.Rhasspy2HermesMQTT ->
                onRhasspy2HermesMQTTSynthesize(
                    sessionId = sessionId,
                    volume = volume,
                    siteId = siteId,
                    handle = handle,
                )

            TextToSpeechOption.Disabled           -> NotSynthesized
        }
    }

    private suspend fun onRhasspy2HermesHttpSynthesize(volume: Float?, siteId: String, handle: Handle): TtsResult {
        if (handle.text == null) return NotSynthesized

        return when (val result = rhasspy2HermesConnection.textToSpeech(
            text = handle.text,
            volume = volume,
            siteId = siteId
        )) {
            is HttpClientResult.HttpClientError -> NotSynthesized
            is HttpClientResult.Success         -> {
                //TODO could return single audio?

                val sampleRate: Int = 0
                val bitRate: Int = 0
                val channel: Int = 0

                Audio(
                    flow {
                        emit(AudioStartEvent)
                        emit(
                            AudioChunkEvent(
                                sampleRate = sampleRate,
                                bitRate = bitRate,
                                channel = channel,
                                data = result.data,
                            )
                        )
                        emit(AudioStopEvent)
                    }
                )
            }
        }
    }

    private suspend fun onRhasspy2HermesMQTTSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult {
        if (handle.text == null) return NotSynthesized

        val requestId = uuid4().toString()

        val result = mqttConnection.say(sessionId, handle.text, volume, siteId, requestId)
        if (result is Error) return NotSynthesized

        //TODO timeout
        val mqttResult = mqttConnection.incomingMessages
            .filterIsInstance<PlayResult>()
            .filter { it.id == requestId }
            .first()

        return when (mqttResult) {
            is PlayBytes -> {
                val sampleRate: Int = 0
                val bitRate: Int = 0
                val channel: Int = 0

                Audio(
                    flow {
                        emit(AudioStartEvent)
                        emit(
                            AudioChunkEvent(
                                sampleRate = sampleRate,
                                bitRate = bitRate,
                                channel = channel,
                                data = mqttResult.byteArray,
                            )
                        )
                        emit(AudioStopEvent)
                    }
                )
            }

            is PlayFinished -> Played
        }
    }

    override fun stop() {}

}