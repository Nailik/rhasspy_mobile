package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class DialogManagementConfigurationViewState(
    override val editData: DialogManagementConfigurationData
) : IConfigurationViewState {

    @Stable
    data class DialogManagementConfigurationData(
        val dialogManagementOption: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
        val textAsrTimeout: Long? = ConfigurationSetting.textAsrTimeout.value,
        val intentRecognitionTimeout: Long? = ConfigurationSetting.intentRecognitionTimeout.value,
        val recordingTimeout: Long? = ConfigurationSetting.recordingTimeout.value
    ) : IConfigurationData {

        val dialogManagementOptionList: ImmutableList<DialogManagementOption> =
            DialogManagementOption.entries.toImmutableList()

        val textAsrTimeoutText: String = textAsrTimeout.toStringOrEmpty()
        val intentRecognitionTimeoutText: String = intentRecognitionTimeout.toStringOrEmpty()
        val recordingTimeoutText: String = recordingTimeout.toStringOrEmpty()

    }

}