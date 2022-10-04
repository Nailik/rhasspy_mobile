package org.rhasspy.mobile.android.settings

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
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun SaveAndRestoreSettingsItem(viewModel: SettingsScreenViewModel) {
    ExpandableListItem(
        text = MR.strings.saveAndRestoreSettings
    ) {
        val openSaveSettingsDialog = remember { mutableStateOf(false) }
        val openRestoreSettingsDialog = remember { mutableStateOf(false) }

        ListElement(
            modifier = Modifier
                .clickable { openSaveSettingsDialog.value = true },
            text = { Text(MR.strings.save) },
            secondaryText = { Text(MR.strings.saveText) })

        ListElement(modifier = Modifier
            .clickable { openRestoreSettingsDialog.value = true },
            text = { Text(MR.strings.restore) },
            secondaryText = { Text(MR.strings.restoreText) })

        if (openSaveSettingsDialog.value) {
            SaveSettingsDialog {
                openSaveSettingsDialog.value = false
                if (it) {
                    viewModel.saveSettingsFile()
                }
            }
        }

        if (openRestoreSettingsDialog.value) {
            RestoreSettingsDialog {
                openRestoreSettingsDialog.value = false
                if (it) {
                    viewModel.restoreSettingsFromFile()
                }
            }
        }
    }
}


@Composable
fun SaveSettingsDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.saveSettings) },
        text = { Text(MR.strings.saveSettingsText, textAlign = TextAlign.Center) },
        icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = MR.strings.warning) },
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

@Composable
fun RestoreSettingsDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.restoreSettings) },
        text = { Text(MR.strings.restoreSettingsText, textAlign = TextAlign.Center) },
        icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = MR.strings.warning) },
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