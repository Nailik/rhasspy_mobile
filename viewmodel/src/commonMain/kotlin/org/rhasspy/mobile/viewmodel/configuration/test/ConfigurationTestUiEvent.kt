package org.rhasspy.mobile.viewmodel.configuration.test

import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestViewState.DialogState

sealed interface ConfigurationTestUiEvent {

    sealed interface Action : ConfigurationTestUiEvent {

        object BackClick : Action

    }

    sealed interface DialogAction : ConfigurationTestUiEvent {

        val dialogState: DialogState

        data class Confirm(override val dialogState: DialogState) : DialogAction
        data class Dismiss(override val dialogState: DialogState) : DialogAction

    }

    sealed interface Change : ConfigurationTestUiEvent {

        object ToggleListFiltered : Change
        object ToggleListAutoscroll : Change
        object OpenServiceStateDialog : Change

    }

}