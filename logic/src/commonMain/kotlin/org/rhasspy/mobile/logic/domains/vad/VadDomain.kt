package org.rhasspy.mobile.logic.domains.vad

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Disabled
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Local
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.AudioChunkEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.VadDomainEvent.VoiceStartedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.VadDomainEvent.VoiceStoppedEvent
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IVadDomain : IService {

    fun onAudioChunk(chunk: AudioChunkEvent)

}

internal class VadDomain(
    private val pipeline: IPipeline
) : IVadDomain {

    override val serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    private val params get() = ConfigurationSetting.vadDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private var isSpeechDetected = false

    private var localSilenceDetection = SilenceDetection(
        automaticSilenceDetectionTime = params.automaticSilenceDetectionTime,
        automaticSilenceDetectionMinimumTime = params.automaticSilenceDetectionMinimumTime,
        automaticSilenceDetectionAudioLevel = params.automaticSilenceDetectionAudioLevel,
    )

    init {
        scope.launch {
            ConfigurationSetting.vadDomainData.data.collectLatest {
                updateState()

                localSilenceDetection = SilenceDetection(
                    automaticSilenceDetectionTime = params.automaticSilenceDetectionTime,
                    automaticSilenceDetectionMinimumTime = params.automaticSilenceDetectionMinimumTime,
                    automaticSilenceDetectionAudioLevel = params.automaticSilenceDetectionAudioLevel,
                )
            }
        }
    }

    private fun updateState() {
        serviceState.value = when (params.voiceActivityDetectionOption) {
            Local    -> ServiceState.Success
            Disabled -> ServiceState.Disabled
        }
    }

    override fun onAudioChunk(chunk: AudioChunkEvent) {
        when (params.voiceActivityDetectionOption) {
            Local    -> {
                val currentlySpeaking = localSilenceDetection.onAudioChunk(chunk)
                if (currentlySpeaking != isSpeechDetected) {
                    isSpeechDetected = currentlySpeaking

                    pipeline.onEvent(
                        when (currentlySpeaking) {
                            true  -> VoiceStartedEvent(Clock.System.now())
                            false -> VoiceStoppedEvent(Clock.System.now())
                        }
                    )
                }
            }

            Disabled -> Unit
        }
    }

}