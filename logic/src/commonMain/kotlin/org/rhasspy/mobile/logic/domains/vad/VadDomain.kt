package org.rhasspy.mobile.logic.domains.vad

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.data.service.option.VadDomainOption.Disabled
import org.rhasspy.mobile.data.service.option.VadDomainOption.Local
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.pipeline.Source
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VadDisabled
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VoiceStopped
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceStart

/**
 * Vad Domain detects speech in an audio stream
 */
internal interface IVadDomain : IDomain {

    suspend fun awaitVoiceStart(audioStream: Flow<MicAudioChunk>): VoiceStart

    suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceEnd

}

/**
 * Vad Domain detects speech in an audio stream
 */
internal class VadDomain(
    private val params: VadDomainData,
    private val domainHistory: IDomainHistory,
) : IVadDomain {

    private val logger = Logger.withTag("VadDomain")

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
                VoiceStart(Source.Local)
            }

            Disabled -> VoiceStart(Source.Local)
        }.also {
            domainHistory.addToHistory(it)
        }
    }


    /**
     * waits for voice to end for Local with timeout (localSilenceDetectionTimeout)
     */
    override suspend fun awaitVoiceStopped(audioStream: Flow<MicAudioChunk>): VoiceEnd {
        logger.d { "awaitVoiceStopped" }
        return when (params.option) {
            Local    -> {
                flow<VoiceEnd> {
                    audioStream
                        .collect { chunk ->
                            if (localSilenceDetection.onAudioChunk(chunk)) {
                                emit(VoiceStopped(Source.Local))
                            }

                        }
                }.first()
            }

            Disabled -> VadDisabled(Source.Local)
        }
    }

    override fun dispose() {}

}