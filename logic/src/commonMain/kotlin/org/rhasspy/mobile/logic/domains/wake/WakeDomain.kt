package org.rhasspy.mobile.logic.domains.wake

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.domain.DomainState.Error
import org.rhasspy.mobile.data.domain.DomainState.NoError
import org.rhasspy.mobile.data.domain.WakeDomainData
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.HotWordDetected
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.pipeline.Source
import org.rhasspy.mobile.logic.pipeline.WakeResult
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient

/**
 * WakeDomain checks for WakeWord within a Flow of MicAudioChunk Events
 */
internal interface IWakeDomain : IDomain {

    val state: StateFlow<DomainState>

    /**
     * collect audioStream until a WakeResult is Detected or NotDetected
     */
    suspend fun awaitDetection(audioStream: Flow<MicAudioChunk>): WakeResult

}

/**
 * WakeDomain checks for WakeWord inside a Flow of MicAudioChunk Events
 */
internal class WakeDomain(
    private val params: WakeDomainData,
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val userConnection: IUserConnection,
    private val domainHistory: IDomainHistory,
) : IWakeDomain {

    private val logger = Logger.withTag("WakeDomain")

    override val state = MutableStateFlow<DomainState>(DomainState.Loading)

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
        scope.launch {
            state.value = when (params.wakeDomainOption) {
                WakeDomainOption.Porcupine          -> initializePorcupine()
                WakeDomainOption.Rhasspy2HermesMQTT -> NoError
                WakeDomainOption.Udp                -> initializeUdp()
                WakeDomainOption.Disabled           -> NoError
            }
        }
    }

    /**
     * collectLatest of audioStream until a WakeResult is Detected or NotDetected
     */
    override suspend fun awaitDetection(audioStream: Flow<MicAudioChunk>): WakeResult {

        val localFlow = flow {
            when (params.wakeDomainOption) {
                WakeDomainOption.Porcupine          -> emit(awaitPorcupineWakeDetection(audioStream))
                WakeDomainOption.Rhasspy2HermesMQTT -> emit(awaitRhasspy2hermesMqttWakeDetection(audioStream))
                WakeDomainOption.Udp                -> emit(awaitUdpWakeDetection(audioStream))
                WakeDomainOption.Disabled           -> Unit
            }
        }
        val remoteFlow = awaitRemoteWakeEvent()

        return merge(
            localFlow,
            remoteFlow,
        ).first().also {
            domainHistory.addToHistory(it)
        }
    }

    /**
     * setup porcupine with params, close old if already exists
     */
    private fun initializePorcupine(): DomainState {
        logger.d { "initializePorcupine" }
        porcupineWakeWordClient.close()
        porcupineWakeWordClient = PorcupineWakeWordClient(
            wakeWordPorcupineAccessToken = params.wakeWordPorcupineAccessToken,
            wakeWordPorcupineKeywordDefaultOptions = params.wakeWordPorcupineKeywordDefaultOptions,
            wakeWordPorcupineKeywordCustomOptions = params.wakeWordPorcupineKeywordCustomOptions,
            wakeWordPorcupineLanguage = params.wakeWordPorcupineLanguage,
        )

        return when (val error = porcupineWakeWordClient.initialize()) {
            is Exception -> Error(TextWrapperString(error.message ?: error.toString()))
            else         -> NoError
        }
    }

    /**
     * setup udp connection with params, close old if already exists
     */
    private suspend fun initializeUdp(): DomainState {
        logger.d { "initializeUdp" }
        udpConnection.close()
        udpConnection = UdpConnection(
            params.wakeWordUdpOutputHost,
            params.wakeWordUdpOutputPort
        )

        return when (val error = udpConnection.connect()) {
            is Exception -> Error(TextWrapperString(error.message ?: error.toString()))
            else         -> NoError
        }
    }

    /**
     * send MicAudioChunk from Flow to Porcupine and return Detection as soon as WakeWord was Detected
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun awaitPorcupineWakeDetection(audioStream: Flow<MicAudioChunk>): WakeResult {
        return audioStream.mapLatest { chunk ->
            with(chunk) {
                val result = porcupineWakeWordClient.audioFrame(
                    sampleRate = sampleRate,
                    encoding = encoding,
                    channel = channel,
                    data = data
                )
                if (result != null) {
                    WakeResult(
                        source = Source.Local,
                        sessionId = null,
                        name = result,
                        timeStamp = Clock.System.now(),
                    )
                } else {
                    null
                }
            }
        }.filterIsInstance<WakeResult>().first()
    }

    /**
     * send to mqtt await for mqtt to send HotWordDetected message
     */
    private suspend fun awaitRhasspy2hermesMqttWakeDetection(audioStream: Flow<MicAudioChunk>): WakeResult {
        val sendDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                with(chunk) {
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
                WakeResult(
                    source = Source.Rhasspy2HermesMqtt,
                    sessionId = null,
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
    private suspend fun awaitUdpWakeDetection(audioStream: Flow<MicAudioChunk>): WakeResult {
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
                WakeResult(
                    source = Source.Local,
                    sessionId = null,
                    name = it.hotWord,
                    timeStamp = Clock.System.now()
                )
            }
            .first()
            .also {
                sendDataJob.cancel()
            }
    }


    private fun awaitRemoteWakeEvent(): Flow<WakeResult> {
        return merge(
            //Mqtt: SessionStarted
            mqttConnection.incomingMessages
                .filterIsInstance<MqttConnectionEvent.SessionStarted>()
                .map {
                    WakeResult(
                        source = Source.Rhasspy2HermesMqtt,
                        sessionId = it.sessionId,
                        name = null,
                        timeStamp = Clock.System.now(),
                    )
                },
            //Mqtt: HotWordDetected
            mqttConnection.incomingMessages
                .filterIsInstance<HotWordDetected>()
                .map {
                    WakeResult(
                        source = Source.Rhasspy2HermesMqtt,
                        sessionId = null,
                        name = it.hotWord,
                        timeStamp = Clock.System.now(),
                    )
                },
            //User: User Button Click
            userConnection.incomingMessages
                .filterIsInstance<UserConnectionEvent.StartStopRhasspy>()
                .map {
                    WakeResult(
                        source = Source.User,
                        sessionId = null,
                        name = null,
                        timeStamp = Clock.System.now(),
                    )
                },
            //Webserver: startRecording, listenForCommand
            webServerConnection.incomingMessages
                .filterIsInstance<WebServerConnectionEvent.StartSession>()
                .map {
                    WakeResult(
                        source = Source.WebServer,
                        sessionId = null,
                        name = null,
                        timeStamp = Clock.System.now(),
                    )
                },
        )
    }

    override fun dispose() {
        porcupineWakeWordClient.close()
        udpConnection.close()
        scope.cancel()
    }

}