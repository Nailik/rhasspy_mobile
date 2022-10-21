package org.rhasspy.mobile.android.permissions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.ui.LocalSnackbarHostState
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.nativeutils.MicrophonePermission

/**
 * to request a microphone permission
 * informationText is to inform user why this is necessary
 * on Result will be called afterwards
 *
 * result can be invoked multiple times (from system dialog and afterwards from snackbar)
 */
@Composable
fun requestMicrophonePermission(
    informationText: StringResource,
    onResult: (granted: Boolean) -> Unit
): () -> Unit {
    //necessary items
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    //info to show if it was denied
    val snackBarMessage = translate(MR.strings.microphonePermissionDenied)
    val snackBarActionLabel = translate(MR.strings.settings)

    //if dialog is opened
    val openRequestPermissionDialog = remember { mutableStateOf(false) }

    if (openRequestPermissionDialog.value) {
        //show info dialog
        MicrophonePermissionInfoDialog(informationText) { result ->
            //hide dialog after user closed information dialog
            openRequestPermissionDialog.value = false
            if (result) {
                //if user click yes, show system permission request
                requestMicrophonePermissionFromSystem(snackBarMessage, snackBarActionLabel, coroutineScope, snackbarHostState, onResult)
            }
        }
    }

    return {
        //check if permission is not yet granted
        if (!MicrophonePermission.granted.value) {
            //check if info dialog is necessary
            if (MicrophonePermission.shouldShowInformationDialog()) {
                openRequestPermissionDialog.value = true
            } else {
                //request directly
                requestMicrophonePermissionFromSystem(snackBarMessage, snackBarActionLabel, coroutineScope, snackbarHostState, onResult)
            }
        } else {
            //permission already granted
            onResult.invoke(true)
        }
    }
}



/**
 * to request the information by showing system dialog
 * result can be invoked multiple times (from system dialog and afterwards from snackbar)
 */
private fun requestMicrophonePermissionFromSystem(
    message: String,
    actionLabel: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onResult: (granted: Boolean) -> Unit
) {
    //request from system
    MicrophonePermission.requestPermission(false) { granted ->
        if (granted) {
            //invoke result when granted
            onResult.invoke(true)
        } else {
            //permission not granted
            onResult.invoke(false)

            //display snackbar when not granted
            coroutineScope.launch {

                val snackbarResult = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short,
                )

                //button to show permission request
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    //open permission
                    MicrophonePermission.requestPermission(true, onResult::invoke)
                }
            }
        }
    }
}

/**
 * show an information dialog about why the permission is required
 */
@Composable
fun MicrophonePermissionInfoDialog(message: StringResource, onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onResult.invoke(false)
        },
        title = {
            Text(MR.strings.microphonePermissionDialogTitle)
        },
        text = {
            Text(message)
        },
        icon = {
            Icon(imageVector = Icons.Filled.Mic, contentDescription = MR.strings.microphone)
        },
        confirmButton = {
            Button(onClick = {
                onResult.invoke(true)
            }) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                onResult.invoke(false)
            }) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}