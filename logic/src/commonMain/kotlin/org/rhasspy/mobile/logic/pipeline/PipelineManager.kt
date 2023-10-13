package org.rhasspy.mobile.logic.pipeline

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.domain.DomainState.Loading
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.impls.PipelineDisabled
import org.rhasspy.mobile.logic.pipeline.impls.PipelineLocal
import org.rhasspy.mobile.logic.pipeline.impls.PipelineMqtt
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IPipelineManager {

    val isPipelineActive: StateFlow<Boolean>
    val isPlayingAudio: StateFlow<Boolean>

    val wakeDomainStateFlow: StateFlow<DomainState>
    val micDomainStateFlow: StateFlow<MicDomainState>
    val micDomainRecordingStateFlow: StateFlow<Boolean>
    val asrDomainRecordingStateFlow: StateFlow<Boolean>

}

internal class PipelineManager(
    private val indication: IIndication,
) : IPipelineManager, KoinComponent {

    private val logger = Logger.withTag("PipelineManager")
    private val params: PipelineData get() = ConfigurationSetting.pipelineData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    override val isPipelineActive = MutableStateFlow(false)
    override val isPlayingAudio = MutableStateFlow(false)

    override val wakeDomainStateFlow = MutableStateFlow<DomainState>(Loading)
    override val micDomainStateFlow = MutableStateFlow<MicDomainState>(MicDomainState.Loading)
    override val micDomainRecordingStateFlow = MutableStateFlow(false)
    override val asrDomainRecordingStateFlow = MutableStateFlow(false)

    private var domainBundle = get<DomainBundle>()

    private var awaitWakeJob: Job? = null
    private var awaitAudioJob: Job? = null

    init {
        initialize()

        scope.launch {
            merge(
                ConfigurationSetting.micDomainData.data,
                ConfigurationSetting.vadDomainData.data,
                ConfigurationSetting.wakeDomainData.data,
                ConfigurationSetting.asrDomainData.data,
                ConfigurationSetting.handleDomainData.data,
                ConfigurationSetting.intentDomainData.data,
                ConfigurationSetting.sndDomainData.data,
                ConfigurationSetting.ttsDomainData.data,
            ).collectLatest {
                if (isPipelineActive.value || isPlayingAudio.value) return@collectLatest

                //restart wake domain
                initialize()
            }
        }
    }

    private fun initialize() {
        isPipelineActive.value = false
        isPlayingAudio.value = false

        domainBundle.dispose()
        domainBundle = get<DomainBundle>()

        collectStateFlows()

        awaitWakeJob = scope.launch {
            if (isPipelineActive.value || isPlayingAudio.value) return@launch

            val wake = domainBundle.wakeDomain.awaitDetection(domainBundle.micDomain.audioStream)
            awaitAudioJob?.cancel()

            isPipelineActive.value = true
            runPipeline(wake, domainBundle)
            initialize()
        }

        awaitAudioJob = scope.launch {
            if (isPipelineActive.value || isPlayingAudio.value) return@launch

            val audio = domainBundle.audioDomain.awaitPlayAudio()
            awaitWakeJob?.cancel()

            isPlayingAudio.value = true
            domainBundle.sndDomain.awaitPlayAudio(audio)
            initialize()
        }

    }

    private fun collectStateFlows() {
        scope.launch {
            domainBundle.wakeDomain.state.collect {
                wakeDomainStateFlow.value = it
            }
        }
        scope.launch {
            domainBundle.micDomain.state.collect {
                micDomainStateFlow.value = it
            }
        }
        scope.launch {
            domainBundle.micDomain.isRecordingState.collect {
                micDomainRecordingStateFlow.value = it
            }
        }
        scope.launch {
            domainBundle.asrDomain.isRecordingState.collect {
                asrDomainRecordingStateFlow.value = it
            }
        }
    }

    private suspend fun runPipeline(wakeResult: WakeResult, domains: DomainBundle) {
        logger.d { "runPipeline $wakeResult" }

        val result = getPipeline(domains).runPipeline(wakeResult)

        logger.d { "runPipeline result $result" }
        indication.onIdle()
    }

    private fun getPipeline(domains: DomainBundle): IPipeline {
        return when (params.option) {
            PipelineManagerOption.Local -> get<PipelineLocal> { parametersOf(params.localPipelineData, domains) }
            PipelineManagerOption.Rhasspy2HermesMQTT -> get<PipelineMqtt> { parametersOf(domains) }
            PipelineManagerOption.Disabled           -> get<PipelineDisabled> { parametersOf(domains) }
        }
    }

}