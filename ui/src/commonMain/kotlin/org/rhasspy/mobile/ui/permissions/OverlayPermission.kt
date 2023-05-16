package org.rhasspy.mobile.ui.permissions

import androidx.compose.runtime.Composable

/**
 * to request a overlay permission
 * informationText is to inform user why this is necessary
 * on Result will be called afterwards
 *
 * result can be invoked multiple times (from system dialog and afterwards from snackbar)
 */
@Composable
fun <T : Any> RequiresOverlayPermission(
    initialData: T,
    onClick: (data: T) -> Unit,
    content: @Composable (onClick: (data: T) -> Unit) -> Unit
) {
 /*   val snackBarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    val snackBarMessage = translate(MR.strings.overlayPermissionRequestFailed.stable)

    var currentData by rememberSaveable { mutableStateOf(initialData) }
    var openRequestPermissionDialog by remember { mutableStateOf(false) }


    if (openRequestPermissionDialog) {
        //show information dialog
        OverlayPermissionInfoDialog { allowRequest ->
            openRequestPermissionDialog = false
            //when user clicked yes redirect him to settings
            if (allowRequest) {

                if (!OverlayPermission.requestPermission { onClick.invoke(currentData) }) {

                    //requesting permission failed, intent did not start
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = snackBarMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                }
            }
        }
    }

    content { data ->
        //check if granted or not
        if (!OverlayPermission.granted.value) {
            //show dialog that permission is necessary
            currentData = data
            openRequestPermissionDialog = true
        } else {
            //permission granted
            onClick.invoke(data)
        }
    }*/
    content{}

}

/**
 * displays information dialog with the reason why overlay permission is required
 */
@Composable
private fun OverlayPermissionInfoDialog(onResult: (result: Boolean) -> Unit) {
   /* AlertDialog(
        onDismissRequest = {
            onResult.invoke(false)
        },
        title = {
            Text(MR.strings.overlayPermissionTitle.stable)
        },
        text = {
            Text(
                resource = MR.strings.overlayPermissionInfo.stable,
                modifier = Modifier.testTag(TestTag.DialogInformationOverlayPermission)
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Layers,
                contentDescription = MR.strings.overlay.stable
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