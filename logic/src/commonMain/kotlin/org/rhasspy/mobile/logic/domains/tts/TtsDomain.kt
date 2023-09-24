package org.rhasspy.mobile.logic.domains.tts

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.TtsDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult.Error
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.snd.SndAudio
import org.rhasspy.mobile.logic.domains.snd.SndAudio.AudioStartEvent
import org.rhasspy.mobile.logic.domains.snd.SndAudio.AudioStopEvent
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.TtsResult
import org.rhasspy.mobile.logic.pipeline.TtsResult.NotSynthesized
import org.rhasspy.mobile.logic.pipeline.TtsResult.PlayFinished

interface ITtsDomain : IService {

    val audioStream: Flow<SndAudio>

    suspend fun onSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult

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

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mqttSessionId = "ITextToSpeechService"

    override val audioStream = MutableSharedFlow<SndAudio>()

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

                val sampleRate: Int = 0
                val bitRate: Int = 0
                val channel: Int = 0

                audioStream.emit(AudioStartEvent)
                //TODO necessary to remove wav header?
                audioStream.emit(
                    SndAudio.AudioChunkEvent(
                        sampleRate = sampleRate,
                        bitRate = bitRate,
                        channel = channel,
                        data = result.data,
                    )
                )
                audioStream.emit(AudioStopEvent)

                PlayFinished
            }
        }
    }

    private suspend fun onRhasspy2HermesMQTTSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult {
        if (handle.text == null) return NotSynthesized

        val requestId = uuid4().toString()

        val result = mqttConnection.say(mqttSessionId, handle.text, volume, siteId, requestId)
        if (result is Error) return NotSynthesized

        //TODO timeout
        //TODO await for play finished
        val mqttResult = mqttConnection.incomingMessages
            .filterIsInstance<PlayResult>()
            .filter { it.id == requestId }
            .first()

        if(mqttResult is PlayResult.PlayBytes) {
            val sampleRate: Int = 0
            val bitRate: Int = 0
            val channel: Int = 0

            audioStream.emit(AudioStartEvent)
            //TODO necessary to remove wav header?
            audioStream.emit(
                SndAudio.AudioChunkEvent(
                    sampleRate = sampleRate,
                    bitRate = bitRate,
                    channel = channel,
                    data = mqttResult.byteArray,
                )
            )
            audioStream.emit(AudioStopEvent)
        }

        return PlayFinished
    }

    override fun stop() {
        scope.cancel()
    }

}