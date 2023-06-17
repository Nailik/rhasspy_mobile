package org.rhasspy.mobile.viewmodel.configuration.edit

sealed interface ConfigurationEditUiEvent {

    sealed interface Action : ConfigurationEditUiEvent {

        object Save : Action
        object Discard : Action
        object BackClick : Action
        object OpenTestScreen : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface Dialog : ConfigurationEditUiEvent {

        object Confirm : Dialog
        object Dismiss : Dialog
        object Close : Dialog

    }

}