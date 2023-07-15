package org.rhasspy.mobile.viewmodel.screens.dialog

sealed interface DialogScreenUiEvent {

    sealed interface Action : DialogScreenUiEvent {

        data object StartSession : Action

        data object StopRecording : Action

    }

}