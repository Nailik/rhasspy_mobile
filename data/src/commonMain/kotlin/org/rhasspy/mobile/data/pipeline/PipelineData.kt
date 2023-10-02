package org.rhasspy.mobile.data.pipeline

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.PipelineManagerOption

@Serializable
data class PipelineData(
    val option: PipelineManagerOption,
)