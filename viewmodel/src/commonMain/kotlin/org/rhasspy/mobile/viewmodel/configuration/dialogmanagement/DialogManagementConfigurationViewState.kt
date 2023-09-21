package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class DialogManagementConfigurationViewState internal constructor(
    override val editData: DialogManagementConfigurationData
) : IConfigurationViewState {

    @Stable
    data class DialogManagementConfigurationData internal constructor(
        val dialogManagementOption: DialogManagementOption,
        val textAsrTimeout: Long?,
        val intentRecognitionTimeout: Long?,
    ) : IConfigurationData {

        val dialogManagementOptionList: ImmutableList<DialogManagementOption> = DialogManagementOption.entries.toTypedArray().toImmutableList()

        val textAsrTimeoutText: String = textAsrTimeout.toStringOrEmpty()
        val intentRecognitionTimeoutText: String = intentRecognitionTimeout.toStringOrEmpty()

    }

}