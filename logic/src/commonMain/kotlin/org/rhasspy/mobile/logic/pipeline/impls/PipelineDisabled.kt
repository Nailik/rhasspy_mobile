package org.rhasspy.mobile.logic.pipeline.impls

import org.rhasspy.mobile.logic.pipeline.DomainBundle
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.StartEvent
import org.rhasspy.mobile.logic.pipeline.PipelineResult
import org.rhasspy.mobile.logic.pipeline.Source

internal class PipelineDisabled(
    private val domains: DomainBundle,
) : IPipeline {

    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {
        //TODO #466 TODO("Not yet implemented")
        return PipelineResult.End(Source.Local)
    }

}