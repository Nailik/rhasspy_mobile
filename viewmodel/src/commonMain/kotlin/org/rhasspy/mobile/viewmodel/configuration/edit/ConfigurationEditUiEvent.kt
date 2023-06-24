package org.rhasspy.mobile.viewmodel.configuration.edit

import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState

sealed interface ConfigurationEditUiEvent {

    sealed interface Action : ConfigurationEditUiEvent {

        object Save : Action
        object Discard : Action
        object BackClick : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface DialogAction : ConfigurationEditUiEvent {

        val dialogState: DialogState

        data class Confirm(override val dialogState: DialogState) : DialogAction
        data class Dismiss(override val dialogState: DialogState) : DialogAction
        data class Close(override val dialogState: DialogState) : DialogAction

    }

}