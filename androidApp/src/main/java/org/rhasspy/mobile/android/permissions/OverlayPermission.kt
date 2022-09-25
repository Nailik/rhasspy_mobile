package org.rhasspy.mobile.android.permissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

/**
 * 3 parts where this is shown:
 *
 * click on record button
 * local WakeWord service
 * click on check audio level button
 */
@Composable
fun OverlayPermissionRequired(viewModel: HomeScreenViewModel) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = viewModel.isOverlayPermissionRequestRequired.collectAsState().value
    ) {
        val overlayPermission = requestOverlayPermission {}

        IconButton(
            onClick = { overlayPermission.invoke() },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            Icon(
                imageVector = Icons.Filled.LayersClear,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                contentDescription = MR.strings.overlay
            )
        }
    }
}

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
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}