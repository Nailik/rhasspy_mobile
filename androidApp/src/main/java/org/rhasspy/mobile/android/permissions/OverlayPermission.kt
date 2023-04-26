package org.rhasspy.mobile.android.permissions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission

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
    val snackBarHostState = LocalSnackbarHostState.current
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
    }

}

/**
 * displays information dialog with the reason why overlay permission is required
 */
@Composable
private fun OverlayPermissionInfoDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
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
    )
}