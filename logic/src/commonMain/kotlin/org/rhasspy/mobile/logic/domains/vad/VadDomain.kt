package org.rhasspy.mobile.logic.domains.vad

import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState
import org.rhasspy.mobile.data.service.ServiceState.Pending
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Disabled
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Local
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.domains.vad.VadEvent.VoiceStart
import org.rhasspy.mobile.logic.domains.vad.VadEvent.VoiceStopped

interface IVadDomain : IService {

    suspend fun awaitVoiceStart(audioStream: Flow<MicAudioChunk>): VoiceStart

    suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceStopped

}

internal class VadDomain(
    private val params: VadDomainData
) : IVadDomain {

    private val logger = Logger.withTag("VadDomain")

    override val hasError: ErrorState? = null

    private var localSilenceDetection = SilenceDetection(
        automaticSilenceDetectionTime = params.automaticSilenceDetectionTime,
        automaticSilenceDetectionMinimumTime = params.automaticSilenceDetectionMinimumTime,
        automaticSilenceDetectionAudioLevel = params.automaticSilenceDetectionAudioLevel,
    )

    override suspend fun awaitVoiceStart(audioStream: Flow<MicAudioChunk>): VoiceStart {
        return when (params.option) {
            Local    -> VoiceStart
            Disabled -> VoiceStart
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceStopped {
        audioStream.mapLatest { chunk ->
            localSilenceDetection.onAudioChunk(chunk)
        }.first { it }
        return VoiceStopped
    }

    override fun dispose() {

    }

}