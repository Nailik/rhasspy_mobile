package org.rhasspy.mobile.viewmodel.screen

import androidx.compose.runtime.Stable

@Stable
data class ScreenViewState(
    val dialogState: ScreenDialogState? = null,
    val snackBarState: ScreenSnackBarState? = null,
) {

    sealed interface ScreenDialogState {

        data object MicrophonePermissionInfo : ScreenDialogState
        data object OverlayPermissionInfo : ScreenDialogState
        data object NotificationPermissionInfo : ScreenDialogState

    }

    sealed interface ScreenSnackBarState {

        data object OverlayPermissionRequestFailed : ScreenSnackBarState
        data object MicrophonePermissionRequestFailed : ScreenSnackBarState

        data object NotificationPermissionRequestFailed : ScreenSnackBarState
        data object MicrophonePermissionRequestDenied : ScreenSnackBarState
        data object LinkOpenFailed : ScreenSnackBarState
        data object SelectFileFailed : ScreenSnackBarState
        data object ScanQRCodeFailed : ScreenSnackBarState

    }
}