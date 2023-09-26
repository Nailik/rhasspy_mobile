package org.rhasspy.mobile.logic.domains.wake

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.domain.WakeDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.HotWordDetected
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.domains.wake.WakeEvent.Detection
import org.rhasspy.mobile.logic.domains.wake.WakeEvent.NotDetected
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient

/**
 * WakeDomain checks for WakeWord within a Flow of MicAudioChunk Events
 */
interface IWakeDomain : IService {

    /**
     * collect audioStream until a WakeResult is Detected or NotDetected
     */
    suspend fun awaitDetection(audioStream: Flow<MicAudioChunk>): WakeEvent

}

/**
 * WakeDomain checks for WakeWord inside a Flow of MicAudioChunk Events
 */
internal class WakeDomain(
    private val params: WakeDomainData,
    private val mqttConnection: IMqttConnection,
) : IWakeDomain {

    private val logger = Logger.withTag("WakeWordService")

    override val serviceState = MutableStateFlow<ServiceState>(Loading)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var porcupineWakeWordClient = PorcupineWakeWordClient(
        wakeWordPorcupineAccessToken = params.wakeWordPorcupineAccessToken,
        wakeWordPorcupineKeywordDefaultOptions = params.wakeWordPorcupineKeywordDefaultOptions,
        wakeWordPorcupineKeywordCustomOptions = params.wakeWordPorcupineKeywordCustomOptions,
        wakeWordPorcupineLanguage = params.wakeWordPorcupineLanguage,
    )

    private var udpConnection = UdpConnection(
        host = params.wakeWordUdpOutputHost,
        port = params.wakeWordUdpOutputPort
    )

    init {
        serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine          -> initializePorcupine()
            WakeWordOption.Rhasspy2HermesMQTT -> Success
            WakeWordOption.Udp                -> initializeUdp()
            WakeWordOption.Disabled           -> Disabled
        }
    }

    /**
     * collectLatest of audioStream until a WakeResult is Detected or NotDetected
     */
    override suspend fun awaitDetection(audioStream: Flow<MicAudioChunk>): WakeEvent {
        return when (params.wakeWordOption) {
            WakeWordOption.Porcupine          -> awaitPorcupineWakeDetection(audioStream)
            WakeWordOption.Rhasspy2HermesMQTT -> awaitRhasspy2hermesMqttWakeDetection(audioStream)
            WakeWordOption.Udp                -> awaitUdpWakeDetection(audioStream)
            WakeWordOption.Disabled           -> NotDetected
        }
    }

    /**
     * setup porcupine with params, close old if already exists
     */
    private fun initializePorcupine(): ServiceState {
        logger.d { "initializePorcupine" }
        porcupineWakeWordClient.close()
        porcupineWakeWordClient = PorcupineWakeWordClient(
            wakeWordPorcupineAccessToken = params.wakeWordPorcupineAccessToken,
            wakeWordPorcupineKeywordDefaultOptions = params.wakeWordPorcupineKeywordDefaultOptions,
            wakeWordPorcupineKeywordCustomOptions = params.wakeWordPorcupineKeywordCustomOptions,
            wakeWordPorcupineLanguage = params.wakeWordPorcupineLanguage,
        )

        return when (val result = porcupineWakeWordClient.initialize()) {
            is Exception -> ErrorState.Exception(result)
            else         -> Success
        }
    }

    /**
     * setup udp connection with params, close old if already exists
     */
    private fun initializeUdp(): ServiceState {
        logger.d { "initializeUdp" }
        udpConnection.close()
        udpConnection = UdpConnection(
            params.wakeWordUdpOutputHost,
            params.wakeWordUdpOutputPort
        )

        return when (val result = udpConnection.connect()) {
            is Exception -> ErrorState.Exception(result)
            else         -> Success
        }
    }

    /**
     * send MicAudioChunk from Flow to Porcupine and return Detection as soon as WakeWord was Detected
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun awaitPorcupineWakeDetection(audioStream: Flow<MicAudioChunk>): WakeEvent {
        return audioStream.mapLatest { chunk ->
            with(chunk) {
                val result = porcupineWakeWordClient.audioFrame(
                    sampleRate = sampleRate,
                    encoding = encoding,
                    channel = channel,
                    data = data
                )
                if (result != null) {
                    Detection(
                        name = result,
                        timeStamp = Clock.System.now()
                    )
                } else {
                    NotDetected
                }
            }
        }.first { it is Detection }
    }

    /**
     * send to mqtt await for mqtt to send HotWordDetected message
     */
    private suspend fun awaitRhasspy2hermesMqttWakeDetection(audioStream: Flow<MicAudioChunk>): WakeEvent {
        val sendDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                with(chunk) {
                    //TODO use result
                    mqttConnection.asrAudioFrame(
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data,
                    )
                }
            }
        }

        return mqttConnection.incomingMessages
            .filterIsInstance<HotWordDetected>()
            .map {
                Detection(
                    name = it.hotWord,
                    timeStamp = Clock.System.now()
                )
            }
            .first()
            .also {
                sendDataJob.cancel()
            }
    }


    /**
     * send to udp and await for mqtt to send HotWordDetected message
     */
    private suspend fun awaitUdpWakeDetection(audioStream: Flow<MicAudioChunk>): WakeEvent {
        val sendDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                with(chunk) {
                    udpConnection.streamAudio(
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data
                    )
                }
            }
        }

        return mqttConnection.incomingMessages
            .filterIsInstance<HotWordDetected>()
            .map {
                Detection(
                    name = it.hotWord,
                    timeStamp = Clock.System.now()
                )
            }
            .first()
            .also {
                sendDataJob.cancel()
            }
    }


    override fun dispose() {
        scope.cancel()
    }

}