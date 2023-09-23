package org.rhasspy.mobile.logic.pipeline

import kotlinx.datetime.Instant

sealed interface PipelineResult



sealed interface TranscriptResult {
    data class Transcript(val intent: String) : TranscriptResult
    data object TranscriptError : TranscriptResult, PipelineResult
}

sealed interface IntentResult {
    data object Intent : IntentResult
    data object NotRecognized : IntentResult, PipelineResult
}
