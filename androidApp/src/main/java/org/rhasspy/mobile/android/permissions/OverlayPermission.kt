package org.rhasspy.mobile.android.permissions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.nativeutils.OverlayPermission

/**
 * 3 parts where this is shown:
 *
 * click on record button
 * local WakeWord service
 * click on check audio level button
 */

@Composable
fun requestOverlayPermission(
    onResult: (granted: Boolean) -> Unit
): () -> Unit {
    val openRequestPermissionDialog = remember { mutableStateOf(false) }

    if (openRequestPermissionDialog.value) {
        OverlayPermissionInfoDialog { result ->
            openRequestPermissionDialog.value = false
            if (result) {
                OverlayPermission.requestPermission {
                    onResult.invoke(it)
                }
            }
        }
    }

    return {
        if (!OverlayPermission.granted.value) {
            openRequestPermissionDialog.value = true
        } else {
            onResult.invoke(true)
        }
    }
}

@Composable
private fun OverlayPermissionInfoDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.overlayPermissionTitle) },
        text = { Text(MR.strings.overlayPermissionInfo) },
        icon = { Icon(imageVector = Icons.Filled.Layers, contentDescription = MR.strings.overlay) },
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