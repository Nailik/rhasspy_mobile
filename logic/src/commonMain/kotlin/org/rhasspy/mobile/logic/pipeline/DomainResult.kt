package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.logic.pipeline.PipelineResult.PipelineErrorResult
import org.rhasspy.mobile.logic.pipeline.domain.Reason

enum class Source {
    Local,
    Rhasspy2HermesHttp,
    Rhasspy2HermesMqtt,
    Rhasspy3WyomingHttp,
    Rhasspy3WyomingWebsocket,
    HomeAssistant,
    WebServer,
    User,
}

sealed interface DomainResult {
    val source: Source
    val timeStamp: Instant
}

sealed interface SndAudio : DomainResult {

    data class AudioStartEvent(
        val sampleRate: Int,
        val bitRate: Int,
        val channel: Int,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndAudio

    class AudioChunkEvent(
        val data: ByteArray,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndAudio

    data class AudioStopEvent(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndAudio

}

data class WakeResult(
    val name: String?,
    val sessionId: String?,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainResult


sealed interface DomainSessionResult : DomainResult {
    val sessionId: String
}

sealed interface PipelineResult : DomainSessionResult {

    data class End(
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : PipelineResult

    sealed interface PipelineErrorResult : PipelineResult {
        val reason: Reason
    }

}


data class PipelineStarted(
    override val sessionId: String,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainSessionResult

data class StartRecording(
    override val sessionId: String,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainSessionResult


sealed interface VadResult : DomainSessionResult {

    data class VoiceStart(
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : VadResult

    sealed interface VoiceEnd : VadResult {

        data class VoiceStopped(
            override val sessionId: String,
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : VoiceEnd

        data class VadError(
            override val sessionId: String,
            override val reason: Reason,
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : PipelineErrorResult, VoiceEnd

    }

}

sealed interface TranscriptResult : DomainSessionResult {
    data class Transcript(
        val text: String,
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult

    data class TranscriptError(
        override val sessionId: String,
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult, PipelineErrorResult

}

sealed interface IntentResult : DomainSessionResult {
    data class Intent(
        val intentName: String?,
        val intent: String,
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult

    data class IntentError(
        override val sessionId: String,
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult, PipelineErrorResult

}

sealed interface HandleResult : IntentResult, DomainSessionResult {
    data class Handle(
        val text: String?,
        val volume: Float?,
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult

    data class HandleError(
        override val sessionId: String,
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult, PipelineErrorResult

}

sealed interface TtsResult : DomainSessionResult {
    data class Audio(
        val data: Flow<SndAudio>,
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult

    data class TtsError(
        override val sessionId: String,
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult, PipelineErrorResult

}

sealed interface SndResult : PipelineResult {
    data class Played(
        val id: String?,
        override val sessionId: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, TtsResult

    data class SndError(
        val id: String?,
        override val sessionId: String,
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, PipelineErrorResult

}