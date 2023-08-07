package org.rhasspy.mobile.viewmodel.screen

import androidx.compose.runtime.Stable

@Stable
data class ScreenViewState internal constructor(
    val dialogState: ScreenDialogState? = null,
    val snackBarState: ScreenSnackBarState? = null
) {

    sealed interface ScreenDialogState {

        data object MicrophonePermissionInfo : ScreenDialogState
        data object OverlayPermissionInfo : ScreenDialogState

    }

    sealed interface ScreenSnackBarState {

        data object OverlayPermissionRequestFailed : ScreenSnackBarState
        data object MicrophonePermissionRequestFailed : ScreenSnackBarState
        data object MicrophonePermissionRequestDenied : ScreenSnackBarState
        data object LinkOpenFailed : ScreenSnackBarState
        data object SelectFileFailed : ScreenSnackBarState
        data object ScanQRCodeFailed : ScreenSnackBarState

    }
}