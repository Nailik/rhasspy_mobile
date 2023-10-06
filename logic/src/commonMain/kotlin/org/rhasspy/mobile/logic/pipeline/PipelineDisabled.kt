package org.rhasspy.mobile.logic.pipeline

internal interface IPipelineDisabled : IPipeline
internal class PipelineDisabled(
    private val domains: DomainBundle,
) : IPipelineDisabled {

    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {
        //TODO #466TODO("Not yet implemented")
        return PipelineResult.End(Source.Local)
    }

}