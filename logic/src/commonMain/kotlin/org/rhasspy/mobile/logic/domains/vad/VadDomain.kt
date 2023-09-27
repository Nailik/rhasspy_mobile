package org.rhasspy.mobile.logic.domains.vad

import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState
import org.rhasspy.mobile.data.service.ServiceState.Pending
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Disabled
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Local
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.domains.vad.VadEvent.*
import org.rhasspy.mobile.logic.domains.vad.VadEvent.VoiceEnd.*
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * Vad Domain detects speech in an audio stream
 */
interface IVadDomain : IService {

    suspend fun awaitVoiceStart(audioStream: Flow<MicAudioChunk>): VoiceStart

    suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceEnd

}

/**
 * Vad Domain detects speech in an audio stream
 */
internal class VadDomain(
    private val params: VadDomainData
) : IVadDomain {

    private val logger = Logger.withTag("VadDomain")

    override val hasError: ErrorState? = null

    private val localSilenceDetection = SilenceDetection(
        automaticSilenceDetectionTime = params.automaticSilenceDetectionTime,
        automaticSilenceDetectionMinimumTime = params.automaticSilenceDetectionMinimumTime,
        automaticSilenceDetectionAudioLevel = params.automaticSilenceDetectionAudioLevel,
    )

    /**
     * waits for voice to start,
     * local and disabled instantly return VoiceStart
     */
    override suspend fun awaitVoiceStart(audioStream: Flow<MicAudioChunk>): VoiceStart {
        logger.d { "awaitVoiceStart" }
        return when (params.option) {
            Local    -> {
                localSilenceDetection.start()
                VoiceStart
            }
            Disabled -> VoiceStart
        }
    }


    /**
     * waits for voice to end for Local with timeout (localSilenceDetectionTimeout)
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceEnd {
        logger.d { "awaitVoiceStopped" }
        return when (params.option) {
            Local    -> {
                audioStream
                    .mapLatest { chunk -> localSilenceDetection.onAudioChunk(chunk) }
                    .filter { it }
                    .map { VoiceStopped }
                    .timeoutWithDefault(
                        timeout = params.timeout,
                        default = VoiceTimeout,
                    ).first()
            }

            Disabled -> {
                delay(params.timeout)
                VoiceTimeout
            }
        }
    }

    override fun dispose() {}

}