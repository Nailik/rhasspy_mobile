package org.rhasspy.mobile.viewmodel.configuration

sealed interface IConfigurationUiEvent {

    sealed interface Action : IConfigurationUiEvent {

        object StartTest : Action
        object StopTest : Action
        object Save : Action
        object Discard : Action
        object BackPress : Action
        object DismissDialog : Action
        object ToggleListFiltered : Action
        object ToggleListAutoscroll : Action

    }

}