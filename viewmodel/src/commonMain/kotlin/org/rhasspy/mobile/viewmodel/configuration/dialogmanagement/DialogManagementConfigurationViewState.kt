package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData

@Stable
data class DialogManagementConfigurationViewState internal constructor(
    override val editData: DialogManagementConfigurationData
) : IConfigurationViewState {

    @Stable
    data class DialogManagementConfigurationData internal constructor(
        val dialogManagementOption: DialogManagementOption,
    ) : IConfigurationData {

        val dialogManagementOptionList = DialogManagementOption.entries.toImmutableList()

    }

}