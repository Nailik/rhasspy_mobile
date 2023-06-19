package org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState

@Stable
data class DialogManagementConfigurationViewState internal constructor(
    val dialogManagementOption: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
) {

    @Stable
    data class DialogManagementConfigurationData internal constructor(
        val dialogManagementOption: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
        val textAsrTimeout: Long? = ConfigurationSetting.textAsrTimeout.value,
        val intentRecognitionTimeout: Long? = ConfigurationSetting.intentRecognitionTimeout.value,
        val recordingTimeout: Long? = ConfigurationSetting.recordingTimeout.value
    ) {

        val dialogManagementOptionList: ImmutableList<DialogManagementOption> = DialogManagementOption.values().toImmutableList()

        val textAsrTimeoutText: String = textAsrTimeout.toString()
        val intentRecognitionTimeoutText: String = intentRecognitionTimeout.toString()
        val recordingTimeoutText: String = recordingTimeout.toString()

    }

}

//
//val textAsrTimeoutText: String = ConfigurationSetting.textAsrTimeout.value.toString(),
//val intentRecognitionTimeoutText: String = ConfigurationSetting.intentRecognitionTimeout.value.toString(),
//val recordingTimeoutText: String = ConfigurationSetting.recordingTimeout.value.toString()
//override val isTestingEnabled: Boolean get() = dialogManagementOption != DialogManagementOption.Disabled