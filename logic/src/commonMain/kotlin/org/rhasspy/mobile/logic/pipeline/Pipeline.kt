package org.rhasspy.mobile.logic.pipeline

import org.rhasspy.mobile.logic.pipeline.PipelineEvent.StartEvent

internal interface IPipeline {

    suspend fun runPipeline(startEvent: StartEvent): PipelineResult

}