package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import org.rhasspy.mobile.logic.domains.snd.SndAudio

internal enum class Source {
    Local,
    Rhasspy2HermesHttp,
    Rhasspy2HermesMqtt,
    HomeAssistant,
    WebServer,
}

internal sealed interface Result {
    val source: Source
}

internal sealed interface PipelineResult : Result {
    data class End(override val source: Source) : PipelineResult
}

internal sealed interface TranscriptResult : Result {
    data class Transcript(val text: String, override val source: Source) : TranscriptResult
    data class TranscriptError(override val source: Source) : TranscriptResult, PipelineResult
    data class TranscriptTimeout(override val source: Source) : TranscriptResult, PipelineResult
    data object TranscriptDisabled : TranscriptResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

internal sealed interface IntentResult : Result {
    data class Intent(val intentName: String?, val intent: String, override val source: Source) : IntentResult
    data class NotRecognized(override val source: Source) : PipelineResult, IntentResult
    data object IntentDisabled : IntentResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

internal sealed interface HandleResult : IntentResult, Result {
    data class Handle(val text: String?, val volume: Float?, override val source: Source) : HandleResult
    data class NotHandled(override val source: Source) : HandleResult, PipelineResult
    data object HandleDisabled : HandleResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

internal sealed interface TtsResult : Result {
    data class Audio(val data: Flow<SndAudio>, override val source: Source) : TtsResult
    data class NotSynthesized(override val source: Source) : TtsResult, PipelineResult
    data object TtsDisabled : TtsResult, PipelineResult {
        override val source: Source = Source.Local
    }
}

internal sealed interface SndResult : Result {
    data class Played(override val source: Source) : SndResult, PipelineResult, TtsResult
    data class NotPlayed(override val source: Source) : SndResult, PipelineResult
    data object PlayDisabled : SndResult, PipelineResult {
        override val source: Source = Source.Local
    }
}