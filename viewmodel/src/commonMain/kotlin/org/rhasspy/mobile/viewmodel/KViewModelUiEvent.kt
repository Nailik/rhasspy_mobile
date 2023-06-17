package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.viewmodel.ScreenViewState.DialogState
import org.rhasspy.mobile.viewmodel.ScreenViewState.SnackBarState

sealed interface KViewModelUiEvent {

    sealed interface Action : KViewModelUiEvent {

        object RequestMicrophonePermission : Action
        object RequestOverlayPermission : Action

    }

    sealed interface SnackBar : KViewModelUiEvent {

        data class Action(val snackBarState: SnackBarState) : SnackBar
        object Consumed : SnackBar

    }

    sealed interface Dialog: KViewModelUiEvent {

        data class Confirm(val dialogState: DialogState) : Dialog
        object Dismiss : Dialog

    }

}