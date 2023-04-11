package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.rhasspy.mobile.data.service.option.DialogManagementOption

sealed interface DialogManagementConfigurationUiAction {

    data class SelectDialogManagementOption(val option: DialogManagementOption) : DialogManagementConfigurationUiAction
    data class ChangeTextAsrTimeout(val value: String) : DialogManagementConfigurationUiAction
    data class ChangeIntentRecognitionTimeout(val value: String) : DialogManagementConfigurationUiAction
    data class ChangeRecordingTimeout(val value: String) : DialogManagementConfigurationUiAction

}