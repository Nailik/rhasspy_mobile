package org.rhasspy.mobile.logic.pipeline

internal interface IPipeline {

    suspend fun runPipeline(wakeResult: WakeResult): PipelineResult

}