package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.PipelineManagerOption

@Stable
data class DialogManagementConfigurationViewState internal constructor(
    val editData: DialogManagementConfigurationData
) {

    @Stable
    data class DialogManagementConfigurationData internal constructor(
        val pipelineManagerOption: PipelineManagerOption,
    ) {

        val pipelineManagerOptionList = PipelineManagerOption.entries.toImmutableList()

    }

}