package org.rhasspy.mobile.logic.pipeline

interface IPipelineDisabled : IPipeline
class PipelineDisabled : IPipelineDisabled {
    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {
        //TODO #466 ("Not yet implemented")
        return PipelineResult.End(Source.Local)
    }
}