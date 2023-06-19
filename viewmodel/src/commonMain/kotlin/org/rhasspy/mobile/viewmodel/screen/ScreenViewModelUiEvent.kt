package org.rhasspy.mobile.viewmodel.screen

sealed interface ScreenViewModelUiEvent {

    sealed interface Action : ScreenViewModelUiEvent {

        object RequestMicrophonePermission : Action
        object RequestOverlayPermission : Action

    }

    sealed interface SnackBar : ScreenViewModelUiEvent {

        data class Action(val snackBarState: ScreenSnackBarState) : SnackBar
        object Consumed : SnackBar

    }

    sealed interface Dialog: ScreenViewModelUiEvent {

        data class Confirm(val dialogState: ScreenDialogState) : Dialog
        object Dismiss : Dialog

    }

}