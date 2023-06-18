package org.rhasspy.mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.SnackBar
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.viewmodel.*
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Dialog.Dismiss
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.SnackBar.Action
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.SnackBar.Consumed
import org.rhasspy.mobile.viewmodel.ScreenViewState.DialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.ScreenViewState.DialogState.OverlayPermissionInfo
import org.rhasspy.mobile.viewmodel.ScreenViewState.SnackBarState.*


val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackBarHostState provided")
}

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No LocalViewModelFactory provided")
}

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    kViewModel: IKViewModel,
    content: @Composable () -> Unit
) {

    val screenViewState by kViewModel.screenViewState.collectAsState()

    DisposableEffect(Unit) {
        kViewModel.onComposed()
        onDispose {
            kViewModel.onDisposed()
        }
    }

    screenViewState.dialogState?.also { dialogState ->
        when (dialogState) {
            MicrophonePermissionInfo -> {
                Dialog(
                    icon = Icons.Filled.Mic,
                    title = MR.strings.microphonePermissionDialogTitle.stable,
                    message = MR.strings.microphonePermissionInfoRecord.stable,
                    confirmLabel = MR.strings.ok.stable,
                    dismissLabel = MR.strings.cancel.stable,
                    onConfirm = { kViewModel.onEvent(Confirm(dialogState)) },
                    onDismiss = { kViewModel.onEvent(Dismiss) }
                )
            }

            OverlayPermissionInfo -> {
                Dialog(
                    icon = Icons.Filled.Layers,
                    title = MR.strings.overlayPermissionTitle.stable,
                    message = MR.strings.overlayPermissionInfo.stable,
                    confirmLabel = MR.strings.ok.stable,
                    dismissLabel = MR.strings.cancel.stable,
                    onConfirm = { kViewModel.onEvent(Confirm(dialogState)) },
                    onDismiss = { kViewModel.onEvent(Dismiss) }
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
                    action = { kViewModel.onEvent(Action(snackBarState)) },
                    consumed = { kViewModel.onEvent(Consumed) }
                )
            }

            MicrophonePermissionRequestFailed -> {
                SnackBar(
                    title = MR.strings.microphonePermissionRequestFailed.stable,
                    consumed = { kViewModel.onEvent(Consumed) },
                )
            }

            OverlayPermissionRequestFailed -> {
                SnackBar(
                    title = MR.strings.overlayPermissionRequestFailed.stable,
                    consumed = { kViewModel.onEvent(Consumed) },
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