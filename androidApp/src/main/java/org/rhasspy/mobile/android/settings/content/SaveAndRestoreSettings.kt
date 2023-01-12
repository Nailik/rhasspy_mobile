package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.settings.SaveAndRestoreSettingsViewModel

/**
 * to save and restore settings
 */
@Preview
@Composable
fun SaveAndRestoreSettingsContent(viewModel: SaveAndRestoreSettingsViewModel = get()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.SaveAndRestoreSettings),
        title = MR.strings.saveAndRestoreSettings
    ) {

        //Save Settings
        SaveSettings(viewModel)

        //Restore Settings
        RestoreSettings(viewModel)

        //Share Settings
        ShareSettings(viewModel)
    }

}

/**
 * Save Settings
 * Shows warning Dialog that the file contains sensitive information
 */
@Composable
private fun SaveSettings(viewModel: SaveAndRestoreSettingsViewModel) {

    var openSaveSettingsDialog by remember { mutableStateOf(false) }

    //save settings
    ListElement(
        modifier = Modifier.clickable { openSaveSettingsDialog = true },
        icon = {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = MR.strings.save
            )
        },
        text = {
            Text(MR.strings.save)
        },
        secondaryText = {
            Text(MR.strings.saveSettingsText)
        }
    )

    //save settings dialog
    if (openSaveSettingsDialog) {
        SaveSettingsDialog(
            onConfirm = {
                openSaveSettingsDialog = false
                viewModel.exportSettingsFile()
            },
            onDismiss = {
                openSaveSettingsDialog = false
            }
        )
    }
}

/**
 * Restore settings
 * shows dialog that current settings will be overwritten
 */
@Composable
private fun RestoreSettings(viewModel: SaveAndRestoreSettingsViewModel) {

    var openRestoreSettingsDialog by remember { mutableStateOf(false) }

    //restore settings
    ListElement(
        modifier = Modifier.clickable { openRestoreSettingsDialog = true },
        icon = {
            Icon(
                imageVector = Icons.Filled.Restore,
                contentDescription = MR.strings.restore
            )
        },
        text = {
            Text(MR.strings.restore)
        },
        secondaryText = {
            Text(MR.strings.restoreSettingsText)
        }
    )

    //restore settings dialog
    if (openRestoreSettingsDialog) {

        RestoreSettingsDialog(
            onConfirm = {
                openRestoreSettingsDialog = false
                viewModel.restoreSettingsFromFile()
            },
            onDismiss = {
                openRestoreSettingsDialog = false
            }
        )

    }
}

@Composable
private fun ShareSettings(viewModel: SaveAndRestoreSettingsViewModel) {

    //restore settings
    ListElement(
        modifier = Modifier.clickable(onClick = viewModel::shareSettingsFile),
        icon = {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = MR.strings.share
            )
        },
        text = {
            Text(MR.strings.share)
        },
        secondaryText = {
            Text(MR.strings.shareSettingsText)
        }
    )

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
                resource = MR.strings.saveSettingsWarningText,
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
                resource = MR.strings.restoreSettingsWarningText,
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