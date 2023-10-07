package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

enum class Source {
    Local,
    Rhasspy2HermesHttp,
    Rhasspy2HermesMqtt,
    HomeAssistant,
    WebServer,
    User,
}

sealed interface PipelineEvent {
    val source: Source
    val timeStamp: Instant
}

sealed interface SndAudio : PipelineEvent {

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

sealed interface PipelineResult : PipelineEvent {
    data class End(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : PipelineResult
}

data class WakeResult(
    override val source: Source,
    override val timeStamp: Instant = Clock.System.now(),
    val sessionId: String?,
    val name: String?,
) : PipelineEvent

sealed interface VadResult : PipelineEvent {

    data class VoiceStart(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : VadResult

    sealed interface VoiceEnd : VadResult {

        data class VoiceStopped(
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : VoiceEnd

        data class VadDisabled(
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : VoiceEnd

        data class VadTimeout(
            override val source: Source,
            override val timeStamp: Instant = Clock.System.now(),
        ) : VoiceEnd

    }

}

sealed interface TranscriptResult : PipelineEvent {
    data class Transcript(
        val text: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult

    data class TranscriptError(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult, PipelineResult

    data class TranscriptTimeout(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult, PipelineResult

    data class TranscriptDisabled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TranscriptResult, PipelineResult

}

sealed interface IntentResult : PipelineEvent {
    data class Intent(
        val intentName: String?,
        val intent: String,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult

    data class NotRecognized(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : PipelineResult, IntentResult

    data class IntentDisabled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : IntentResult, PipelineResult
}

sealed interface HandleResult : IntentResult, PipelineEvent {
    data class Handle(
        val text: String?,
        val volume: Float?,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult

    data class NotHandled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult, PipelineResult

    data class HandleTimeout(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult, PipelineResult

    data class HandleDisabled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : HandleResult, PipelineResult

}

sealed interface TtsResult : PipelineEvent {
    data class Audio(
        val data: Flow<SndAudio>,
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult

    data class NotSynthesized(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult, PipelineResult

    data class TtsDisabled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult, PipelineResult

    data class TtsTimeout(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : TtsResult, PipelineResult

}

sealed interface SndResult : PipelineEvent, PipelineResult {
    data class Played(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, TtsResult

    data class NotPlayed(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult

    data class PlayDisabled(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult

    data class SndTimeout(
        override val source: Source,
        override val timeStamp: Instant = Clock.System.now(),
    ) : SndResult, TtsResult

}