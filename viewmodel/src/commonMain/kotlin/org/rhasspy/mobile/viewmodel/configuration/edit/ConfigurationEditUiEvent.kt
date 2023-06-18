package org.rhasspy.mobile.viewmodel.configuration.edit

import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.Dialog

sealed interface ConfigurationEditUiEvent {

    sealed interface Action : ConfigurationEditUiEvent {

        object Save : Action
        object Discard : Action
        object BackClick : Action
        object OpenTestScreen : Action
        object OpenServiceStateDialog : Action

    }

    sealed interface DialogAction : ConfigurationEditUiEvent {

        val dialog: Dialog

        data class Confirm(override val dialog: Dialog) : DialogAction
        data class Dismiss(override val dialog: Dialog) : DialogAction
        data class Close(override val dialog: Dialog) : DialogAction

    }

}