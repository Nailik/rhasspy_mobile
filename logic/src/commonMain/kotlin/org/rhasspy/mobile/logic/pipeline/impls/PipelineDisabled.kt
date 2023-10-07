package org.rhasspy.mobile.logic.pipeline.impls

import org.rhasspy.mobile.logic.pipeline.*

internal class PipelineDisabled(
    private val domains: DomainBundle,
) : IPipeline {

    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {
        //TODO #466 TODO("Not yet implemented")
        return PipelineResult.End(Source.Local)
    }

}