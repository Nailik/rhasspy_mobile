package org.rhasspy.mobile.logic.pipeline.impls

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.pipeline.*

internal class PipelineDisabled(
    private val domains: DomainBundle,
) : IPipeline {

    private val logger = Logger.withTag("PipelineDisabled")

    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {
        logger.d { "runPipeline $wakeResult" }
        //TODO #466 TODO("Not yet implemented")
        return PipelineResult.End(Source.Local)
    }

}