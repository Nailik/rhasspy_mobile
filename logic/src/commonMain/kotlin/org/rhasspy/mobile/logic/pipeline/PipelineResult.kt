package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.rhasspy.mobile.logic.domains.snd.SndAudio

enum class Source {
    Local,
    Rhasspy2HermesHttp,
    Rhasspy2HermesMqtt,
    HomeAssistant,
    WebServer,
    User,
}

sealed interface Result {
    val source: Source
}

sealed interface PipelineResult : Result {
    data class End(override val source: Source) : PipelineResult
}

data class WakeResult(
    override val source: Source,
    val sessionId: String?,
    val name: String?,
    val timeStamp: Instant,
) : Result

sealed interface VadResult : Result {

    data class VoiceStart(override val source: Source) : VadResult

    sealed interface VoiceEnd : VadResult {

        data class VoiceStopped(override val source: Source) : VoiceEnd
        data class VadDisabled(override val source: Source) : VoiceEnd
        data class VadTimeout(override val source: Source) : VoiceEnd

    }

}

sealed interface TranscriptResult : Result {
    data class Transcript(val text: String, override val source: Source) : TranscriptResult
    data class TranscriptError(override val source: Source) : TranscriptResult, PipelineResult
    data class TranscriptTimeout(override val source: Source) : TranscriptResult, PipelineResult
    data object TranscriptDisabled : TranscriptResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

sealed interface IntentResult : Result {
    data class Intent(val intentName: String?, val intent: String, override val source: Source) : IntentResult
    data class NotRecognized(override val source: Source) : PipelineResult, IntentResult
    data object IntentDisabled : IntentResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

sealed interface HandleResult : IntentResult, Result {
    data class Handle(val text: String?, val volume: Float?, override val source: Source) : HandleResult
    data class NotHandled(override val source: Source) : HandleResult, PipelineResult
    data class HandleTimeout(override val source: Source) : HandleResult, PipelineResult
    data object HandleDisabled : HandleResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

sealed interface TtsResult : Result {
    data class Audio(val data: Flow<SndAudio>, override val source: Source) : TtsResult
    data class NotSynthesized(override val source: Source) : TtsResult, PipelineResult
    data object TtsDisabled : TtsResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

sealed interface SndResult : Result, PipelineResult {
    data class Played(override val source: Source) : SndResult, TtsResult
    data class NotPlayed(override val source: Source) : SndResult
    data object PlayDisabled : SndResult {
        override val source: Source = Source.Local
    }
}