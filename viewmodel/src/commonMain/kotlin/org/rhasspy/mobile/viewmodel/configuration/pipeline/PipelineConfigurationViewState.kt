package org.rhasspy.mobile.viewmodel.configuration.pipeline

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.PipelineManagerOption

@Stable
data class PipelineConfigurationViewState internal constructor(
    val editData: PipelineConfigurationData
) {

    @Stable
    data class PipelineConfigurationData internal constructor(
        val pipelineManagerOption: PipelineManagerOption,
    ) {

        val pipelineManagerOptionList = PipelineManagerOption.entries.toImmutableList()

    }

}