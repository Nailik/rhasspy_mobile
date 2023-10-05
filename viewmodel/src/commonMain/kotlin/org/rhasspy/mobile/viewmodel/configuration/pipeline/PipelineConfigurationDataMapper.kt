package org.rhasspy.mobile.viewmodel.configuration.pipeline

import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData

class PipelineConfigurationDataMapper {

    operator fun invoke(data: PipelineData): PipelineConfigurationData {
        return PipelineConfigurationData(
            pipelineManagerOption = data.option,
        )
    }

    operator fun invoke(data: PipelineConfigurationData): PipelineData {
        return PipelineData(
            option = data.pipelineManagerOption,
        )
    }

}