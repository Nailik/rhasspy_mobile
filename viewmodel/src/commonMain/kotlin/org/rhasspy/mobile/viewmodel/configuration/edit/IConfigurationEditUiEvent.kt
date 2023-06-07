package org.rhasspy.mobile.viewmodel.configuration.edit

sealed interface IConfigurationEditUiEvent {

    sealed interface Action : IConfigurationEditUiEvent {

        object Save : Action
        object Discard : Action
        object SaveDialog : Action
        object DiscardDialog : Action
        object DismissDialog : Action
        object BackClick : Action
        object OpenTestScreen : Action
        object OpenServiceStateDialog : Action
        object CloseServiceStateDialog : Action

    }

}