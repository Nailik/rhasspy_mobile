package org.rhasspy.mobile.viewmodel.screen

import androidx.compose.runtime.Stable

@Stable
data class ScreenViewState internal constructor(
    val dialogState: ScreenDialogState? = null,
    val snackBarState: ScreenSnackBarState? = null
) {

    sealed interface ScreenDialogState {

        object MicrophonePermissionInfo: ScreenDialogState
        object OverlayPermissionInfo: ScreenDialogState

    }

    sealed interface ScreenSnackBarState {

        object OverlayPermissionRequestFailed: ScreenSnackBarState
        object MicrophonePermissionRequestFailed: ScreenSnackBarState
        object MicrophonePermissionRequestDenied: ScreenSnackBarState

    }
}