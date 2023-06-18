package org.rhasspy.mobile.viewmodel.configuration.test

sealed interface ConfigurationTestUiEvent {

    sealed interface Action : ConfigurationTestUiEvent {

        object BackClick : Action
        object ToggleListFiltered : Action
        object ToggleListAutoscroll : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface Dialog : ConfigurationTestUiEvent {

        object Confirm : Dialog
        object Dismiss : Dialog

    }

}