package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString
import org.rhasspy.mobile.logic.pipeline.PipelineResult.PipelineErrorResult

enum class Source {
    Local,
    Rhasspy2HermesHttp,
    Rhasspy2HermesMqtt,
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

sealed interface PipelineResult : DomainResult {
    data class End(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : PipelineResult

    sealed interface PipelineErrorResult : PipelineResult {

        val reason: Reason

    }
}

data class WakeResult(
    val name: String?,
    val sessionId: String?,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainResult


data class PipelineStarted(
    val sessionId: String,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainResult

data class StartRecording(
    val sessionId: String,
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
) : DomainResult

sealed interface Reason {

    data object Disabled : Reason

    data object Timeout : Reason

    data class Error(val information: TextWrapper) : Reason {

        constructor(text: String) : this(TextWrapperString(text))
        constructor(resource: StableStringResource) : this(TextWrapperStableStringResource(resource))
        constructor(exception: Exception) : this(TextWrapperString("$exception ${exception.message}"))

    }
}

sealed interface VadResult : DomainResult {

    data class VoiceStart(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : VadResult

    sealed interface VoiceEnd : VadResult {

        data class VoiceStopped(
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : VoiceEnd

        data class VadError(
            override val reason: Reason,
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : PipelineErrorResult, VoiceEnd

    }

}

sealed interface TranscriptResult : DomainResult {
    data class Transcript(
        val text: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult

    data class TranscriptError(
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult, PipelineErrorResult

}

sealed interface IntentResult : DomainResult {
    data class Intent(
        val intentName: String?,
        val intent: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult

    data class IntentError(
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult, PipelineErrorResult

}

sealed interface HandleResult : IntentResult, DomainResult {
    data class Handle(
        val text: String?,
        val volume: Float?,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult

    data class HandleError(
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult, PipelineErrorResult

}

sealed interface TtsResult : DomainResult {
    data class Audio(
        val data: Flow<SndAudio>,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult

    data class TtsError(
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult, PipelineErrorResult

}

sealed interface SndResult : DomainResult, PipelineResult {
    data class Played(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, TtsResult

    data class SndError(
        override val reason: Reason,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, PipelineErrorResult

}