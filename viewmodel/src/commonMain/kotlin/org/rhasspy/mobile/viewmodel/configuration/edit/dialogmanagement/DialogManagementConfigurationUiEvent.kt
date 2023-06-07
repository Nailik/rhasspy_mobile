package org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement

import org.rhasspy.mobile.data.service.option.DialogManagementOption

sealed interface DialogManagementConfigurationUiEvent {

    sealed interface Change : DialogManagementConfigurationUiEvent {

        data class SelectDialogManagementOption(val option: DialogManagementOption) : Change
        data class ChangeTextAsrTimeout(val timeout: String) : Change
        data class ChangeIntentRecognitionTimeout(val timeout: String) : Change
        data class ChangeRecordingTimeout(val timeout: String) : Change

    }

    sealed interface Action : DialogManagementConfigurationUiEvent {

        object BackClick : Action

    }

}