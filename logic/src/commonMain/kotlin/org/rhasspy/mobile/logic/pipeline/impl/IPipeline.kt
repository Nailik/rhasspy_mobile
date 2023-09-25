package org.rhasspy.mobile.logic.pipeline.impl

import org.rhasspy.mobile.logic.pipeline.PipelineResult

interface IPipeline {

    suspend fun runPipeline(sessionId: String): PipelineResult

}