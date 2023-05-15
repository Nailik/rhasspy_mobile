package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.Navigator

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    service: DialogManagerService,
    navigator: Navigator
) : IConfigurationViewModel<DialogManagementConfigurationViewState>(
    service = service,
    initialViewState = ::DialogManagementConfigurationViewState,
    navigator = navigator
) {

    fun onEvent(event: DialogManagementConfigurationUiEvent) {
        updateViewState {
            when (event) {
                is ChangeIntentRecognitionTimeout -> it.copy(intentRecognitionTimeoutText = event.timeout)
                is ChangeRecordingTimeout -> it.copy(recordingTimeoutText = event.timeout)
                is ChangeTextAsrTimeout -> it.copy(textAsrTimeoutText = event.timeout)
                is SelectDialogManagementOption -> it.copy(dialogManagementOption = event.option)
            }
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.dialogManagementOption.value = data.dialogManagementOption
        ConfigurationSetting.textAsrTimeout.value = data.textAsrTimeout
        ConfigurationSetting.intentRecognitionTimeout.value = data.intentRecognitionTimeout
        ConfigurationSetting.recordingTimeout.value = data.recordingTimeout
    }

}