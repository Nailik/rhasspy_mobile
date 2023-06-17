package org.rhasspy.mobile.viewmodel

import androidx.compose.runtime.Stable

@Stable
data class ScreenViewState internal constructor(
    val dialogState: DialogState? = null,
    val snackBarState: SnackBarState? = null
) {

    sealed interface DialogState {

        object MicrophonePermissionInfo: DialogState
        object OverlayPermissionInfo: DialogState

    }


    sealed interface SnackBarState {

        object OverlayPermissionRequestFailed: SnackBarState
        object MicrophonePermissionRequestFailed: SnackBarState
        object MicrophonePermissionRequestDenied: SnackBarState

    }

}