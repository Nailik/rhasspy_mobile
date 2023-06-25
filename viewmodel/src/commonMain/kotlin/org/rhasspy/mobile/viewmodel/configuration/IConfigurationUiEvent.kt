package org.rhasspy.mobile.viewmodel.configuration

import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.DialogState

sealed interface IConfigurationUiEvent {

    sealed interface Action : IConfigurationUiEvent {

        object Save : Action
        object Discard : Action
        object BackClick : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface DialogAction : IConfigurationUiEvent {

        val dialogState: DialogState

        data class Confirm(override val dialogState: DialogState) : DialogAction
        data class Dismiss(override val dialogState: DialogState) : DialogAction
        data class Close(override val dialogState: DialogState) : DialogAction

    }

}