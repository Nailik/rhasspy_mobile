package org.rhasspy.mobile.viewmodel.configuration.test

sealed interface IConfigurationTestUiEvent {

    sealed interface Action : IConfigurationTestUiEvent {

        object StartTest : Action
        object StopTest : Action
        object ToggleListFiltered : Action
        object ToggleListAutoscroll : Action
        object BackClick : Action
        object OpenServiceStateDialog : Action
        object CloseServiceStateDialog : Action

    }

}