package org.rhasspy.mobile.android.permissions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.nativeutils.OverlayPermission

/**
 * to request a overlay permission
 * informationText is to inform user why this is necessary
 * on Result will be called afterwards
 *
 * result can be invoked multiple times (from system dialog and afterwards from snackbar)
 */
@Composable
fun requestOverlayPermission(
    onResult: (granted: Boolean) -> Unit
): () -> Unit {
    var openRequestPermissionDialog by remember { mutableStateOf(false) }

    if (openRequestPermissionDialog) {
        //show information dialog
        OverlayPermissionInfoDialog {
            openRequestPermissionDialog = false
            //when user clicked yes redirect him to settings
            if (it) {
                OverlayPermission.requestPermission(onResult::invoke)
            }
        }
    }

    return {
        //check if granted or not
        if (!OverlayPermission.granted.value) {
            openRequestPermissionDialog = true
        } else {
            onResult.invoke(true)
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
            Text(MR.strings.overlayPermissionTitle)
        },
        text = {
            Text(MR.strings.overlayPermissionInfo)
        },
        icon = {
            Icon(imageVector = Icons.Filled.Layers, contentDescription = MR.strings.overlay)
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