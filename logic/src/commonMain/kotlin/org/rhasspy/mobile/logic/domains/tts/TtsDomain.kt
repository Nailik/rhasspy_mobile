package org.rhasspy.mobile.logic.domains.tts

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.TtsDomainData
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayBytes
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult.Error
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.snd.SndAudio.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TtsResult
import org.rhasspy.mobile.logic.pipeline.TtsResult.*
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeaderBitRate
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeaderChannel
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeaderSampleRate
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * sends text from Handle and converts it into audio chunks that are returned via TtsResult
 */
interface ITtsDomain : IDomain {

    suspend fun onSynthesize(
        sessionId: String,
        volume: Float?,
        siteId: String,
        handle: Handle,
    ): TtsResult

}

/**
 * sends text from Handle and converts it into audio chunks that are returned via TtsResult
 */
internal class TtsDomain(
    private val params: TtsDomainData,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection
) : ITtsDomain {

    private val logger = Logger.withTag("TextToSpeechService")

    /**
     * sends text and returned audio stream via TtsResult
     * in case of text is null NotSynthesized is returned
     */
    override suspend fun onSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult {
        logger.d { "onSynthesize for sessionId $sessionId and volume $volume and siteId $siteId and handle $handle" }

        return when (params.option) {
            TtsDomainOption.Rhasspy2HermesHttp ->
                onRhasspy2HermesHttpSynthesize(
                    volume = volume,
                    siteId = siteId,
                    handle = handle,
                )

            TtsDomainOption.Rhasspy2HermesMQTT ->
                onRhasspy2HermesMQTTSynthesize(
                    sessionId = sessionId,
                    volume = volume,
                    siteId = siteId,
                    handle = handle,
                )

            TtsDomainOption.Disabled           ->
                TtsDisabled
        }
    }

    /**
     * sends text to Rhasspy2HermesHttp and receive bytearray of wav data
     */
    private suspend fun onRhasspy2HermesHttpSynthesize(volume: Float?, siteId: String, handle: Handle): TtsResult {
        logger.d { "onRhasspy2HermesHttpSynthesize for volume $volume and siteId $siteId and handle $handle" }

        if (handle.text == null) return NotSynthesized(Local)

        return when (val result = rhasspy2HermesConnection.textToSpeech(
            text = handle.text,
            volume = volume,
            siteId = siteId
        )) {
            is HttpClientResult.HttpClientError -> NotSynthesized(Rhasspy2HermesHttp)
            is HttpClientResult.Success         -> {
                Audio(
                    data = flow {
                        emit(
                            AudioStartEvent(
                                sampleRate = result.data.getWavHeaderSampleRate(),
                                bitRate = result.data.getWavHeaderBitRate(),
                                channel = result.data.getWavHeaderChannel(),
                            )
                        )
                        emit(
                            AudioChunkEvent(
                                data = result.data,
                            )
                        )
                        emit(AudioStopEvent)
                    },
                    source = Rhasspy2HermesHttp,
                )
            }
        }
    }

    /**
     * sends text to Rhasspy2HermesHttp and waits for PlayResult with timeout
     */
    private suspend fun onRhasspy2HermesMQTTSynthesize(sessionId: String, volume: Float?, siteId: String, handle: Handle): TtsResult {
        logger.d { "onRhasspy2HermesHttpSynthesize for volume $volume and siteId $siteId and handle $handle" }

        if (handle.text == null) return NotSynthesized(Local)

        val requestId = uuid4().toString()

        val result = mqttConnection.say(sessionId, handle.text, volume, siteId, requestId)
        if (result is Error) return NotSynthesized(Rhasspy2HermesMqtt)

        return mqttConnection.incomingMessages
            .filterIsInstance<PlayResult>()
            .filter { it.id == requestId }
            .map { mapMqttPlayResult(it) }
            .timeoutWithDefault(
                timeout = params.rhasspy2HermesMqttTimeout,
                default = NotSynthesized(Local)
            )
            .first()
    }

    private fun mapMqttPlayResult(result: PlayResult): TtsResult {
        return when (result) {
            is PlayBytes    -> {
                Audio(
                    data = flow {
                        emit(
                            AudioStartEvent(
                                sampleRate = result.byteArray.getWavHeaderSampleRate(),
                                bitRate = result.byteArray.getWavHeaderBitRate(),
                                channel = result.byteArray.getWavHeaderChannel(),
                            )
                        )
                        emit(
                            AudioChunkEvent(
                                data = result.byteArray,
                            )
                        )
                        emit(AudioStopEvent)
                    },
                    source = Rhasspy2HermesHttp,
                )
            }

            is PlayFinished -> Played(source = Rhasspy2HermesHttp)
        }
    }

    override fun dispose() {}

}