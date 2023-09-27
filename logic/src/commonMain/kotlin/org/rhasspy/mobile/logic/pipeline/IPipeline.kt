package org.rhasspy.mobile.logic.pipeline

interface IPipeline {

    suspend fun runPipeline(startEvent: StartEvent): PipelineResult

}