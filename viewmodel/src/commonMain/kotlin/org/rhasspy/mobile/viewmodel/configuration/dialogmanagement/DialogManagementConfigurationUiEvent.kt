package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface DialogManagementConfigurationUiEvent {

    sealed interface Change : DialogManagementConfigurationUiEvent {

        data class SelectDialogManagementOption(val option: DialogManagementOption) : Change
        data class ChangeTextAsrTimeout(val timeout: String) : Change
        data class ChangeIntentRecognitionTimeout(val timeout: String) : Change
        data class ChangeRecordingTimeout(val timeout: String) : Change

    }

    sealed interface Action : DialogManagementConfigurationUiEvent {

        data object BackClick : Action
        data class Navigate(val destination: NavigationDestination) : Action

    }

}