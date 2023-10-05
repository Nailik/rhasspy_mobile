package org.rhasspy.mobile.viewmodel.configuration.pipeline

import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface PipelineConfigurationUiEvent {

    sealed interface Change : PipelineConfigurationUiEvent {

        data class SelectPipelineOption(val option: PipelineManagerOption) : Change

    }

    sealed interface Action : PipelineConfigurationUiEvent {

        data class Navigate(val destination: NavigationDestination) : Action

    }

}