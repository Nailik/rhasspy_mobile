package org.rhasspy.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action.MicrophonePermissionDialogResult
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action.RequestMicrophonePermissionRedirect
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Consumed.ConsumedMicrophonePermissionSnackBar
import org.rhasspy.mobile.viewmodel.ViewModelFactory


val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackBarHostState provided")
}

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No LocalViewModelFactory provided")
}


@Composable
fun Screen(
    viewModel: KViewModel,
    content: @Composable () -> Unit
) {

    DisposableEffect(viewModel) {
        viewModel.composed()
        onDispose {
            viewModel.disposed()
        }
    }

    PermissionHandling(viewModel)

    content()

}

@Composable
private fun PermissionHandling(
    viewModel: KViewModel
) {
    val kViewState by viewModel.kViewState.collectAsState()

    if (kViewState.isShowMicrophonePermissionInformationDialog) {
        MicrophonePermissionInfoDialog(
            message = MR.strings.microphonePermissionInfoRecord.stable,
            onResult = { viewModel.onEvent(MicrophonePermissionDialogResult(it)) }
        )
    }

    kViewState.microphonePermissionSnackBarText?.also { message ->

        val snackBarMessage = translate(message)
        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarActionLabel = kViewState.microphonePermissionSnackBarLabel?.let { translate(it) }

        LaunchedEffect(snackBarMessage) {

            val snackBarResult = snackBarHostState.showSnackbar(
                message = snackBarMessage,
                actionLabel = snackBarActionLabel,
                duration = SnackbarDuration.Short,
            )

            viewModel.onEvent(ConsumedMicrophonePermissionSnackBar)

            if (snackBarResult == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(RequestMicrophonePermissionRedirect)
            }
        }
    }

}

@Composable
private fun MicrophonePermissionInfoDialog(
    message: StableStringResource,
    onResult: (result: Boolean) -> Unit
) {

    Dialog(
        onDismissRequest = {
            onResult.invoke(false)
        },
        headline = {
            Text(MR.strings.microphonePermissionDialogTitle.stable)
        },
        supportingText = {
            Text(
                resource = message,
                modifier = Modifier.testTag(TestTag.DialogInformationMicrophonePermission)
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = MR.strings.microphone.stable
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onResult.invoke(true)
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.ok.stable)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onResult.invoke(false)
                },
                modifier = Modifier.testTag(TestTag.DialogCancel)
            ) {
                Text(MR.strings.cancel.stable)
            }
        }
    )

}
