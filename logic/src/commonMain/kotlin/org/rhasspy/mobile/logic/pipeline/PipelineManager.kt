package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.domain.DomainState.Loading
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.HotWordDetected
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.SessionStarted
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent.StartStopRhasspy
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.StartSession
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.domains.wake.WakeEvent.Detection
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult.HandleDisabled
import org.rhasspy.mobile.logic.pipeline.HandleResult.NotHandled
import org.rhasspy.mobile.logic.pipeline.IntentResult.IntentDisabled
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.NotSynthesized
import org.rhasspy.mobile.logic.pipeline.TtsResult.TtsDisabled
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IPipelineManager {

    val wakeDomainStateFlow: StateFlow<DomainState>
    val micDomainStateFlow: StateFlow<MicDomainState>
    val micDomainRecordingStateFlow: StateFlow<Boolean>
    val asrDomainRecordingStateFlow: StateFlow<Boolean>

}

internal class PipelineManager(
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val userConnection: IUserConnection,
    private val indication: IIndication,
) : IPipelineManager, KoinComponent {

    private val params: PipelineData = ConfigurationSetting.pipelineData.value

    private val scope = CoroutineScope(Dispatchers.IO)
    private var pipelineScope = CoroutineScope(Dispatchers.IO)

    private var pipelineJob: Job? = null

    private var wakeDomain: IWakeDomain? = null

    override val wakeDomainStateFlow = MutableStateFlow<DomainState>(Loading)
    override val micDomainStateFlow = MutableStateFlow<MicDomainState>(MicDomainState.Loading)
    override val micDomainRecordingStateFlow = MutableStateFlow(false)
    override val asrDomainRecordingStateFlow = MutableStateFlow(false)

    init {
        scope.launch {
            ConfigurationSetting.wakeDomainData.data.collect {
                if (pipelineJob != null) return@collect

                pipelineScope.cancel()
                //restart wake domain
                wakeDomain?.dispose()
                awaitPipelineStart()
            }
        }
    }

    //TODO #466 allow:
    //Mqtt: PlayBytes
    //WebServer: WebServerPlayWav, WebServerSay
    //Local: PlayRecording
    private suspend fun awaitPipelineStart() {
        pipelineScope = CoroutineScope(Dispatchers.IO)
        pipelineJob = null

        val domains = get<DomainBundle>()
        val currentWakeDomain = get<IWakeDomain>()
        wakeDomain = currentWakeDomain.apply {
            awaitDetection(domains.micDomain.audioStream)
        }

        collectStateFlows(currentWakeDomain, domains)

        pipelineScope.launch {
            val startEvent = merge(
                //Mqtt: SessionStarted
                mqttConnection.incomingMessages
                    .filterIsInstance<SessionStarted>()
                    .map {
                        StartEvent(
                            sessionId = it.sessionId,
                            wakeWord = "Rhasspy2HermesMqtt_SessionStarted",
                        )
                    },
                //Mqtt: HotWordDetected
                mqttConnection.incomingMessages
                    .filterIsInstance<HotWordDetected>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = it.hotWord,
                        )
                    },
                //User: User Button Click
                userConnection.incomingMessages
                    .filterIsInstance<StartStopRhasspy>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = "ManualUser",
                        )
                    },
                //Webserver: startRecording, listenForCommand
                webServerConnection.incomingMessages
                    .filterIsInstance<StartSession>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = "Rhasspy2HermesHttp",
                        )
                    },
                //Local: Local WakeWord
                currentWakeDomain.wakeEvents
                    .filterIsInstance<Detection>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = it.name,
                        )
                    },
            ).first()

            //run pipeline with event
            runPipeline(startEvent, domains)
        }
    }

    private fun collectStateFlows(wakeDomain: IWakeDomain, domains: DomainBundle) {
        pipelineScope.launch {
            wakeDomain.state.collect {
                wakeDomainStateFlow.value = it
            }
        }
        pipelineScope.launch {
            domains.micDomain.state.collect {
                micDomainStateFlow.value = it
            }
        }
        pipelineScope.launch {
            domains.micDomain.isRecordingState.collect {
                micDomainRecordingStateFlow.value = it
            }
        }
        pipelineScope.launch {
            domains.asrDomain.isRecordingState.collect {
                asrDomainRecordingStateFlow.value = it
            }
        }
    }

    private fun runPipeline(startEvent: StartEvent, domains: DomainBundle) {
        pipelineJob = pipelineScope.launch {
            when (getPipeline(domains).runPipeline(startEvent)) {
                is End,
                is HandleDisabled,
                is IntentDisabled,
                is PlayDisabled,
                is Played,
                is TtsDisabled,
                is TranscriptDisabled -> {
                    //Success: indication idle
                    indication.onIdle()
                }

                is NotHandled,
                is NotPlayed,
                is NotRecognized,
                is NotSynthesized,
                is TranscriptError,
                is TranscriptTimeout  -> {
                    //Error: indication error
                    indication.onIdle()
                    indication.onError()
                }
            }
            awaitPipelineStart()
        }
    }

    private fun getPipeline(domains: DomainBundle): IPipeline {
        return when (params.option) {
            PipelineManagerOption.Local              -> get<IPipelineLocal> { parametersOf(domains) }
            PipelineManagerOption.Rhasspy2HermesMQTT -> get<IPipelineMqtt> { parametersOf(domains) }
            PipelineManagerOption.Disabled           -> get<IPipelineDisabled> { parametersOf(domains) }
        }
    }

}