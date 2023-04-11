package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeIntentRecognitionTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeRecordingTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeTextAsrTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.SelectDialogManagementOption

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
class DialogManagementConfigurationViewModel(
    service: DialogManagerService,
    testRunner: DialogManagementConfigurationTest
) : IConfigurationViewModel<DialogManagementConfigurationTest, DialogManagementConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = DialogManagementConfigurationViewState()
) {

    fun onAction(action: DialogManagementConfigurationUiAction) {
        contentViewState.update {
            when (action) {
                is ChangeIntentRecognitionTimeout -> it.copy(intentRecognitionTimeoutText = action.value)
                is ChangeRecordingTimeout -> it.copy(recordingTimeoutText = action.value)
                is ChangeTextAsrTimeout -> it.copy(textAsrTimeoutText = action.value)
                is SelectDialogManagementOption -> it.copy(dialogManagementOption = action.option)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.dialogManagementOption.value = data.dialogManagementOption
        ConfigurationSetting.textAsrTimeout.value = data.textAsrTimeout
        ConfigurationSetting.intentRecognitionTimeout.value = data.intentRecognitionTimeout
        ConfigurationSetting.recordingTimeout.value = data.recordingTimeout
    }

    override fun initializeTestParams() {
        get<DialogManagerServiceParams> {
            parametersOf(
                DialogManagerServiceParams(
                    option = data.dialogManagementOption,
                    asrTimeout = data.textAsrTimeout,
                    intentRecognitionTimeout = data.intentRecognitionTimeout,
                    recordingTimeout = data.recordingTimeout
                )
            )
        }
    }

}