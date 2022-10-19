package org.rhasspy.mobile.android.screens.mainNavigation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.settings.SaveAndRestoreSettingsViewModel

/**
 * to save and restore settings
 */
@Preview
@Composable
fun SaveAndRestoreSettingsContent(viewModel: SaveAndRestoreSettingsViewModel = viewModel()) {

    PageContent(MR.strings.saveAndRestoreSettings) {

        val openSaveSettingsDialog = remember { mutableStateOf(false) }
        val openRestoreSettingsDialog = remember { mutableStateOf(false) }

        //save settings
        ListElement(
            modifier = Modifier.clickable { openSaveSettingsDialog.value = true },
            text = {
                Text(MR.strings.save)
            },
            secondaryText = {
                Text(MR.strings.saveText)
            }
        )

        //save settings dialog
        if (openSaveSettingsDialog.value) {
            SaveSettingsDialog(
                onConfirm = {
                    openSaveSettingsDialog.value = false
                    viewModel.saveSettingsFile()
                },
                onDismiss = {
                    openSaveSettingsDialog.value = false
                }
            )
        }

        //restore settings
        ListElement(
            modifier = Modifier.clickable { openRestoreSettingsDialog.value = true },
            text = {
                Text(MR.strings.restore)
            },
            secondaryText = {
                Text(MR.strings.restoreText)
            }
        )

        //restore settings dialog
        if (openRestoreSettingsDialog.value) {

            RestoreSettingsDialog(
                onConfirm = {
                    openRestoreSettingsDialog.value = false
                    viewModel.restoreSettingsFromFile()
                },
                onDismiss = {
                    openRestoreSettingsDialog.value = false
                }
            )

        }

    }

}

/**
 * dialog to save settings
 */
@Composable
private fun SaveSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(MR.strings.saveSettings)
        },
        text = {
            Text(
                resource = MR.strings.saveSettingsText,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

}

/**
 * dialog to restore settings
 */
@Composable
private fun RestoreSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(MR.strings.restoreSettings)
        },
        text = {
            Text(
                resource = MR.strings.restoreSettingsText,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

}