package org.rhasspy.mobile.viewmodel.configuration.edit

sealed interface ConfigurationEditUiEvent {

    sealed interface Action : ConfigurationEditUiEvent {

        object Save : Action
        object Discard : Action
        object BackClick : Action
        object OpenTestScreen : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface DialogAction : ConfigurationEditUiEvent {

        val dialogState: ConfigurationDialogState

        data class Confirm(override val dialogState: ConfigurationDialogState) : DialogAction
        data class Dismiss(override val dialogState: ConfigurationDialogState) : DialogAction
        data class Close(override val dialogState: ConfigurationDialogState) : DialogAction

    }

}