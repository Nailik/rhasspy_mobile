package org.rhasspy.mobile.viewmodel.configuration.pipeline

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface PipelineConfigurationUiEvent {

    sealed interface Change : PipelineConfigurationUiEvent {

        data class SelectPipelineOption(val option: PipelineManagerOption) : Change

    }

    sealed interface PipelineLocalUiEvent : PipelineConfigurationUiEvent {

        sealed interface Change : PipelineLocalUiEvent {

            data class SetSoundIndicationEnabled(val enabled: Boolean) : Change
            data class SelectSoundIndicationOutputOption(val option: AudioOutputOption) : Change

        }

    }


    sealed interface Action : PipelineConfigurationUiEvent {

        data class Navigate(val destination: NavigationDestination) : Action

    }


}