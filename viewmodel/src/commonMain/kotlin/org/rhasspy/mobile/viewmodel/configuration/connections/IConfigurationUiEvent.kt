package org.rhasspy.mobile.viewmodel.configuration.connections

import org.rhasspy.mobile.viewmodel.configuration.connections.ConfigurationViewState.DialogState

sealed interface IConfigurationUiEvent {

    sealed interface Action : IConfigurationUiEvent {

        data object Save : Action
        data object Discard : Action
        data object BackClick : Action
        data object OpenServiceStateDialog : Action

    }

    sealed interface DialogAction : IConfigurationUiEvent {

        val dialogState: DialogState

        data class Confirm(override val dialogState: DialogState) : DialogAction
        data class Dismiss(override val dialogState: DialogState) : DialogAction
        data class Close(override val dialogState: DialogState) : DialogAction

    }

}