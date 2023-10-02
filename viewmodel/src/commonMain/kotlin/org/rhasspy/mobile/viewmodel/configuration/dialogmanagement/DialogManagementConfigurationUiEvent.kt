package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface DialogManagementConfigurationUiEvent {

    sealed interface Change : DialogManagementConfigurationUiEvent {

        data class SelectDialogManagementOption(val option: PipelineManagerOption) : Change

    }

    sealed interface Action : DialogManagementConfigurationUiEvent {

        data class Navigate(val destination: NavigationDestination) : Action

    }

}