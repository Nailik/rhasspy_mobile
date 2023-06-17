package org.rhasspy.mobile.viewmodel.configuration.test

sealed interface ConfigurationTestUiEvent {

    sealed interface Action : ConfigurationTestUiEvent {

        object BackClick : Action

    }

}