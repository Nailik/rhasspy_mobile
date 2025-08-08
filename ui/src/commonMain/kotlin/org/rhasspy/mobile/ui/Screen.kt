package org.rhasspy.mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.SnackBar
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Dismiss
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.SnackBar.Action
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.SnackBar.Consumed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.LinkOpenFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.MicrophonePermissionRequestDenied
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.MicrophonePermissionRequestFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.OverlayPermissionRequestFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.ScanQRCodeFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.SelectFileFailed


val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackBarHostState provided")
}

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No LocalViewModelFactory provided")
}

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    screenViewModel: IScreenViewModel,
    content: @Composable () -> Unit
) {

    val screenViewState by screenViewModel.screenViewState.collectAsState()

    DisposableEffect(Unit) {
        screenViewModel.onComposed()
        onDispose {
            screenViewModel.onDisposed()
        }
    }

    screenViewState.dialogState?.also { dialogState ->
        when (dialogState) {
            MicrophonePermissionInfo -> {
                Dialog(
                    testTag = TestTag.DialogMicrophonePermissionInfo,
                    icon = Icons.Filled.Mic,
                    title = MR.strings.microphonePermissionDialogTitle.stable,
                    message = MR.strings.microphonePermissionInfoRecord.stable,
                    confirmLabel = MR.strings.ok.stable,
                    dismissLabel = MR.strings.cancel.stable,
                    onConfirm = { screenViewModel.onEvent(Confirm(dialogState)) },
                    onDismiss = { screenViewModel.onEvent(Dismiss) }
                )
            }

            OverlayPermissionInfo -> {
                Dialog(
                    testTag = TestTag.DialogOverlayPermissionInfo,
                    icon = Icons.Filled.Layers,
                    title = MR.strings.overlayPermissionTitle.stable,
                    message = MR.strings.overlayPermissionInfo.stable,
                    confirmLabel = MR.strings.ok.stable,
                    dismissLabel = MR.strings.cancel.stable,
                    onConfirm = { screenViewModel.onEvent(Confirm(dialogState)) },
                    onDismiss = { screenViewModel.onEvent(Dismiss) }
                )
            }
        }
    }

    screenViewState.snackBarState?.also { snackBarState ->
        when (snackBarState) {
            MicrophonePermissionRequestDenied -> {
                SnackBar(
                    title = MR.strings.microphonePermissionDenied.stable,
                    label = MR.strings.settings.stable,
                    action = { screenViewModel.onEvent(Action(snackBarState)) },
                    consumed = { screenViewModel.onEvent(Consumed) }
                )
            }

            MicrophonePermissionRequestFailed -> {
                SnackBar(
                    title = MR.strings.microphonePermissionRequestFailed.stable,
                    label = MR.strings.settings.stable,
                    action = { screenViewModel.onEvent(Action(snackBarState)) },
                    consumed = { screenViewModel.onEvent(Consumed) },
                )
            }

            OverlayPermissionRequestFailed -> {
                SnackBar(
                    title = MR.strings.overlayPermissionRequestFailed.stable,
                    consumed = { screenViewModel.onEvent(Consumed) },
                )
            }

            LinkOpenFailed -> {
                SnackBar(
                    title = MR.strings.linkOpenFailed.stable,
                    consumed = { screenViewModel.onEvent(Consumed) },
                )
            }

            SelectFileFailed -> {
                SnackBar(
                    title = MR.strings.selectFileFailed.stable,
                    consumed = { screenViewModel.onEvent(Consumed) },
                )
            }

            ScanQRCodeFailed -> {
                SnackBar(
                    title = MR.strings.scan_qr_code_failed.stable,
                    consumed = { screenViewModel.onEvent(Consumed) },
                )
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        content()
    }

}