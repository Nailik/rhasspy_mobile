package org.rhasspy.mobile.viewmodel.configuration.audioinput

sealed interface AudioInputConfigurationUiEvent {

    sealed interface Change : AudioInputConfigurationUiEvent

    sealed interface Action : AudioInputConfigurationUiEvent {

        data object BackClick : Action

    }

}