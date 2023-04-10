package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationContentViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

@Stable
data class DialogManagementConfigurationViewState(
    val dialogManagementOptionList: ImmutableList<DialogManagementOption>,
    val dialogManagementOption: DialogManagementOption,
    val textAsrTimeoutText: String,
    val textAsrTimeout: Long,
    val intentRecognitionTimeoutText: String,
    val intentRecognitionTimeout: Long,
    val recordingTimeoutText: String,
    val recordingTimeout: Long
): IConfigurationContentViewState {

    companion object {
        fun getInitial() = DialogManagementConfigurationViewState(
            dialogManagementOptionList = DialogManagementOption.values().toList().toImmutableList(),
            dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
            textAsrTimeoutText = ConfigurationSetting.textAsrTimeout.value.toString(),
            textAsrTimeout = ConfigurationSetting.textAsrTimeout.value,
            intentRecognitionTimeoutText = ConfigurationSetting.intentRecognitionTimeout.value.toString(),
            intentRecognitionTimeout = ConfigurationSetting.intentRecognitionTimeout.value,
            recordingTimeoutText = ConfigurationSetting.recordingTimeout.value.toString(),
            recordingTimeout = ConfigurationSetting.recordingTimeout.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges =
            !(dialogManagementOption == ConfigurationSetting.dialogManagementOption.value &&
                    textAsrTimeout == ConfigurationSetting.textAsrTimeout.value &&
                    intentRecognitionTimeout == ConfigurationSetting.intentRecognitionTimeout.value &&
                    recordingTimeout == ConfigurationSetting.recordingTimeout.value),
            isTestingEnabled = dialogManagementOption != DialogManagementOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.dialogManagementOption.value = dialogManagementOption
        ConfigurationSetting.textAsrTimeout.value = textAsrTimeout
        ConfigurationSetting.intentRecognitionTimeout.value = intentRecognitionTimeout
        ConfigurationSetting.recordingTimeout.value = recordingTimeout
    }

}