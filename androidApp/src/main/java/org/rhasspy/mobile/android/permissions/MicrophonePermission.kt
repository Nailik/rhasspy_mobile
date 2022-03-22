package org.rhasspy.mobile.android.permissions

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.android.utils.Text

/**
 * 3 parts where this is shown:
 *
 * click on record button
 * local WakeWord service
 * click on check audio level button
 */

@Composable
fun requestMicrophonePermission(
    snackbarHostState: SnackbarHostState,
    informationText: StringResource,
    onResult: (granted: Boolean) -> Unit
): () -> Unit {
    val openRequestPermissionDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val activity = LocalContext.current as ComponentActivity

    val snackBarMessage = translate(MR.strings.microphonePermissionDenied)
    val snackBarActionLabel = translate(MR.strings.settings)

    if (openRequestPermissionDialog.value) {
        MicrophonePermissionInfoDialog(informationText) { result ->
            openRequestPermissionDialog.value = false
            if (result) {
                requestMicrophonePermissionFromSystem(snackBarMessage, snackBarActionLabel, coroutineScope, snackbarHostState, onResult)
            }
        }
    }

    return {
        if (!MicrophonePermission.granted.value) {
            if (MicrophonePermission.shouldShowInfoDialog(activity)) {
                openRequestPermissionDialog.value = true
            } else {
                requestMicrophonePermissionFromSystem(snackBarMessage, snackBarActionLabel, coroutineScope, snackbarHostState, onResult)
            }
        } else {
            onResult.invoke(true)
        }
    }
}


private fun requestMicrophonePermissionFromSystem(
    message: String,
    actionLabel: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onResult: (granted: Boolean) -> Unit
) {
    MicrophonePermission.requestPermission(false) { granted ->
        if (granted) {
            onResult.invoke(true)
        } else if (!granted) {

            coroutineScope.launch {

                val snackbarResult = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short,
                )

                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    MicrophonePermission.requestPermission(true) {
                        onResult.invoke(it)
                    }
                } else {
                    onResult.invoke(false)
                }
            }
        }
    }
}

@Composable
private fun MicrophonePermissionInfoDialog(message: StringResource, onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.microphonePermissionDialogTitle) },
        text = { Text(message) },
        icon = { org.rhasspy.mobile.android.utils.Icon(imageVector = Icons.Filled.Mic, contentDescription = MR.strings.microphone) },
        confirmButton = {
            Button(onClick = { onResult.invoke(true) }) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onResult.invoke(false) }) {
                Text(MR.strings.cancel)
            }
        }
    )
}