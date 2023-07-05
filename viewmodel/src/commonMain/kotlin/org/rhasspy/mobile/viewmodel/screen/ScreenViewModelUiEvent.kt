package org.rhasspy.mobile.viewmodel.screen

import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState

sealed interface ScreenViewModelUiEvent {

    sealed interface Action : ScreenViewModelUiEvent {

        data object RequestMicrophonePermission : Action
        data object RequestOverlayPermission : Action

    }

    sealed interface SnackBar : ScreenViewModelUiEvent {

        data class Action(val snackBarState: ScreenSnackBarState) : SnackBar
        data object Consumed : SnackBar

    }

    sealed interface Dialog : ScreenViewModelUiEvent {

        data class Confirm(val dialogState: ScreenDialogState) : Dialog
        data object Dismiss : Dialog

    }

}