package org.rhasspy.mobile.viewmodel.configuration.audioinput

sealed interface AudioInputConfigurationUiEvent {

    sealed interface Action : AudioInputConfigurationUiEvent {

        data object BackClick : Action
        data object OpenInputFormatConfigurationScreen : Action
        data object OpenOutputFormatConfigurationScreen : Action

    }

}