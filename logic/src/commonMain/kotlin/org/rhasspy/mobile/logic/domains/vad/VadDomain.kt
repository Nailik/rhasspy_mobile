package org.rhasspy.mobile.logic.domains.vad

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.data.service.option.VadDomainOption.Disabled
import org.rhasspy.mobile.data.service.option.VadDomainOption.Local
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.pipeline.Source
import org.rhasspy.mobile.logic.pipeline.StartRecording
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VadError
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VoiceStopped
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceStart
import org.rhasspy.mobile.logic.pipeline.domain.Reason
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * Vad Domain detects speech in an audio stream
 */
internal interface IVadDomain : IDomain {

    suspend fun awaitVoiceStart(startRecording: StartRecording, audioStream: Flow<MicAudioChunk>): VoiceStart

    suspend fun awaitVoiceStopped(startRecording: StartRecording, audioStream: Flow<MicAudioChunk>): VoiceEnd

}

/**
 * Vad Domain detects speech in an audio stream
 */
internal class VadDomain(
    private val params: VadDomainData,
    private val userConnection: IUserConnection,
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
    override suspend fun awaitVoiceStart(startRecording: StartRecording, audioStream: Flow<MicAudioChunk>): VoiceStart {
        logger.d { "awaitVoiceStart" }
        return when (params.option) {
            Local    -> {
                localSilenceDetection.start()
                VoiceStart(
                    sessionId = startRecording.sessionId,
                    source = Source.Local,
                )
            }

            Disabled -> VoiceStart(
                sessionId = startRecording.sessionId,
                source = Source.Local,
            )
        }.also {
            domainHistory.addToHistory(startRecording, it)
        }
    }


    /**
     * waits for voice to end for Local with timeout (localSilenceDetectionTimeout)
     */
    override suspend fun awaitVoiceStopped(startRecording: StartRecording, audioStream: Flow<MicAudioChunk>): VoiceEnd {
        logger.d { "awaitVoiceStopped" }
        return when (params.option) {
            Local    -> awaitVoiceStoppedLocal(startRecording, audioStream)
            Disabled -> awaitVoiceStoppedDisabled(startRecording)
        }.also {
            domainHistory.addToHistory(startRecording, it)
        }
    }

    private suspend fun awaitVoiceStoppedLocal(startRecording: StartRecording, audioStream: Flow<MicAudioChunk>): VoiceEnd {
        return merge(
            flow<VoiceEnd> {
                audioStream
                    .collect { chunk ->
                        if (localSilenceDetection.onAudioChunk(chunk)) {
                            emit(
                                VoiceStopped(
                                    sessionId = startRecording.sessionId,
                                    source = Source.Local,
                                )
                            )
                        }
                    }
            },
            userConnection.incomingMessages
                .filterIsInstance<UserConnectionEvent.StartStopRhasspy>()
                .map {
                    VoiceStopped(
                        sessionId = startRecording.sessionId,
                        source = Source.User,
                    )
                },
        ).timeoutWithDefault(
            timeout = params.voiceTimeout,
            default = VadError(
                sessionId = startRecording.sessionId,
                reason = Reason.Timeout,
                source = Source.Local,
            ),
        ).first()
    }


    private suspend fun awaitVoiceStoppedDisabled(startRecording: StartRecording): VoiceEnd {
        return userConnection.incomingMessages
            .filterIsInstance<UserConnectionEvent.StartStopRhasspy>()
            .map {
                VoiceStopped(
                    sessionId = startRecording.sessionId,
                    source = Source.User,
                )
            }
            .timeoutWithDefault(
                timeout = params.voiceTimeout,
                default = VadError(
                    sessionId = startRecording.sessionId,
                    reason = Reason.Timeout,
                    source = Source.Local,
                ),
            )
            .first()
    }

    override fun dispose() {}

}