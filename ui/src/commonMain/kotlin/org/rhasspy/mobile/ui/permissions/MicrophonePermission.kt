package org.rhasspy.mobile.ui.permissions


import androidx.compose.runtime.Composable
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.runtime.rememberCoroutineScope
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.main.LocalSnackbarHostState

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
    informationText: StableStringResource,
    onClick: () -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    val snackBarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    val snackBarMessage = translate(MR.strings.microphonePermissionDenied.stable)
    val snackBarMessageError = translate(MR.strings.microphonePermissionRequestFailed.stable)
    val snackBarActionLabel = translate(MR.strings.settings.stable)
/*
    //launcher to get result of system request
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            MicrophonePermission.update()
            onClick()
        } else {
            //snackBar to open app system settings
            coroutineScope.launch {

                val snackBarResult = snackBarHostState.showSnackbar(
                    message = snackBarMessage,
                    actionLabel = snackBarActionLabel,
                    duration = SnackbarDuration.Short,
                )

                if (snackBarResult == SnackbarResult.ActionPerformed) {

                    if (ExternalRedirect.launch(ExternalRedirectIntention.RequestMicrophonePermissionExternally) !is ExternalRedirectResult.Success) {

                        snackBarHostState.showSnackbar(
                            message = snackBarMessageError,
                            duration = SnackbarDuration.Short,
                        )

                    }

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
            onClick()
        }
    }
*/
    content {}
}

/**
 * show an information dialog about why the permission is required
 */
@NoLiveLiterals
@Composable
private fun MicrophonePermissionInfoDialog(
    message: StableStringResource,
    onResult: (result: Boolean) -> Unit
) {
/*
    AlertDialog(
        onDismissRequest = {
            onResult.invoke(false)
        },
        title = {
            Text(MR.strings.microphonePermissionDialogTitle.stable)
        },
        text = {
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
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )*/

}

