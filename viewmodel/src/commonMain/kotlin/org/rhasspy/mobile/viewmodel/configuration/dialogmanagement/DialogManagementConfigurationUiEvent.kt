package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface DialogManagementConfigurationUiEvent {

    sealed interface Change : DialogManagementConfigurationUiEvent {

        data class SelectDialogManagementOption(val option: DialogManagementOption) : Change

    }

    sealed interface Action : DialogManagementConfigurationUiEvent {

        data class Navigate(val destination: NavigationDestination) : Action

    }

}