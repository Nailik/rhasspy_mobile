package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.Flow
import org.rhasspy.mobile.logic.domains.snd.SndAudio

sealed interface PipelineResult {
    data object End: PipelineResult
}

sealed interface TranscriptResult {
    data class Transcript(val text: String) : TranscriptResult
    data object TranscriptError : TranscriptResult, PipelineResult
}

sealed interface IntentResult {
    data class Intent(val intentName: String?, val intent: String) : IntentResult
    data object NotRecognized :PipelineResult, IntentResult
}

sealed interface HandleResult: IntentResult {
    data class Handle(val text: String?) : HandleResult
    data object NotHandled : HandleResult, PipelineResult
}

sealed interface TtsResult {
    data class Audio(val data: Flow<SndAudio>) : TtsResult
    data object NotSynthesized : TtsResult, PipelineResult
}

sealed interface SndResult {
    data object Played : SndResult, PipelineResult, TtsResult
}