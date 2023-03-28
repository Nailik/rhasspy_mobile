package org.rhasspy.mobile.android.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission

/**
 * Wraps a composable that requires the Microphone Permission before click
 * information Text is the text shown in the information dialog
 * onClick is used when the permission is granted
 * content is the content to be shown
 *
 * if necessary shows information dialog
 * then shows system permission dialog
 * when denied a snackbar toast is shown with a link to the app system settings
 */
@Composable
fun RequiresMicrophonePermission(
    informationText: StringResource,
    onClick: () -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    val snackBarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    val snackBarMessage = translate(MR.strings.microphonePermissionDenied)
    val snackBarActionLabel = translate(MR.strings.settings)

    //launcher to get result of system request
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onClick.invoke()
        } else {
            //snackBar to open app system settings
            coroutineScope.launch {

                val snackBarResult = snackBarHostState.showSnackbar(
                    message = snackBarMessage,
                    actionLabel = snackBarActionLabel,
                    duration = SnackbarDuration.Short,
                )

                if (snackBarResult == SnackbarResult.ActionPerformed) {
                    MicrophonePermission.requestPermissionExternally()
                }
            }
        }
    }


    //shows information dialog
    var openRequestPermissionDialog by rememberSaveable { mutableStateOf(false) }

    if (openRequestPermissionDialog) {
        MicrophonePermissionInfoDialog(informationText) { result ->
            openRequestPermissionDialog = false
            if (result) {
                //if user click yes, show system permission request
                launcher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    content {
        //check if permission is not yet granted
        if (!MicrophonePermission.granted.value) {
            //check if info dialog is necessary
            if (MicrophonePermission.shouldShowInformationDialog()) {
                openRequestPermissionDialog = true
            } else {
                //request directly
                launcher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            //permission granted
            onClick.invoke()
        }
    }

}

/**
 * show an information dialog about why the permission is required
 */
@NoLiveLiterals
@Composable
private fun MicrophonePermissionInfoDialog(
    message: StringResource,
    onResult: (result: Boolean) -> Unit
) {

    AlertDialog(
        onDismissRequest = {
            onResult.invoke(false)
        },
        title = {
            Text(MR.strings.microphonePermissionDialogTitle)
        },
        text = {
            Text(
                resource = message,
                modifier = Modifier.testTag(TestTag.DialogInformationMicrophonePermission)
            )
        },
        icon = {
            Icon(imageVector = Icons.Filled.Mic, contentDescription = MR.strings.microphone)
        },
        confirmButton = {
            Button(
                onClick = {
                    onResult.invoke(true)
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onResult.invoke(false)
                },
                modifier = Modifier.testTag(TestTag.DialogCancel)
            ) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

}

