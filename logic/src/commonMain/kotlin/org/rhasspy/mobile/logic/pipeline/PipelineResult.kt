package org.rhasspy.mobile.logic.pipeline

sealed interface PipelineResult

sealed interface TranscriptResult {
    data class Transcript(val text: String) : TranscriptResult
    data object TranscriptError : TranscriptResult, PipelineResult
}

sealed interface IntentResult {
    data class Intent(val intentName: String?, val intent: String) : IntentResult
    data object NotRecognized : IntentResult, PipelineResult
}

sealed interface HandleResult: IntentResult {
    data class Handle(val text: String?) : HandleResult
    data object NotHandled : HandleResult, PipelineResult
}

sealed interface TtsResult {
    data object PlayFinished : TtsResult, PipelineResult
    data object NotSynthesized : TtsResult, PipelineResult
}