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
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult.*
import org.rhasspy.mobile.logic.pipeline.IntentResult.IntentDisabled
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.*
import org.rhasspy.mobile.logic.pipeline.impls.PipelineDisabled
import org.rhasspy.mobile.logic.pipeline.impls.PipelineLocal
import org.rhasspy.mobile.logic.pipeline.impls.PipelineMqtt
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
    private val fileStorage: IFileStorage,
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


    private suspend fun awaitPipelineStart() {
        pipelineScope = CoroutineScope(Dispatchers.IO)
        pipelineJob = null

        val domains = get<DomainBundle>()
        val currentWakeDomain = get<IWakeDomain>()
        wakeDomain = currentWakeDomain

        collectStateFlows(currentWakeDomain, domains)

        pipelineScope.launch {
            when (
                val startEvent = merge(
                    flow { emit(currentWakeDomain.awaitDetection(domains.micDomain.audioStream)) },
                    // playAudioFlow()  //TODO #466
                ).first()
            ) {
                is WakeResult -> runPipeline(startEvent, domains)
                //   is PlayAudioEvent -> {} //TODO #466
            }
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

    private fun runPipeline(wakeResult: WakeResult, domains: DomainBundle) {
        pipelineJob = pipelineScope.launch {
            when (getPipeline(domains).runPipeline(wakeResult)) {
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
                is HandleTimeout,
                is NotPlayed,
                is NotRecognized,
                is NotSynthesized,
                is TranscriptError,
                is SndTimeout,
                is TtsTimeout,
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
            PipelineManagerOption.Local              -> get<PipelineLocal> { parametersOf(domains) }
            PipelineManagerOption.Rhasspy2HermesMQTT -> get<PipelineMqtt> { parametersOf(domains) }
            PipelineManagerOption.Disabled           -> get<PipelineDisabled> { parametersOf(domains) }
        }
    }


    //TODO #466 allow:
    /*
        private fun playAudioFlow(): Flow<PlayAudioEvent> {
            return merge(
                //Mqtt: PlayBytes
                mqttConnection.incomingMessages
                    .filterIsInstance<PlayBytes>()
                    .map {
                        PlayAudioEvent(AudioSource.Data(it.byteArray))
                    },
                //WebServer: WebServerPlayWav, WebServerSay
                webServerConnection.incomingMessages
                    .filterIsInstance<WebServerPlayWav>()
                    .map {
                        PlayAudioEvent(AudioSource.Data(it.data))
                    },
                //Local: PlayRecording
                userConnection.incomingMessages
                    .filterIsInstance<StartStopPlayRecording>()
                    .map {
                        PlayAudioEvent(AudioSource.File(fileStorage.speechToTextAudioFile))
                    },
            )
        }*/


}