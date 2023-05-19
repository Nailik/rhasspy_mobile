package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.SaveAndRestoreSettings
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel

/**
 * to save and restore settings
 */
@Preview
@Composable
fun SaveAndRestoreSettingsContent() {
    val viewModel: SaveAndRestoreSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackbarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        SettingsScreenItemContent(
            modifier = Modifier.testTag(SaveAndRestoreSettings),
            title = MR.strings.saveAndRestoreSettings.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            //Save Settings
            SaveSettings(viewModel::onEvent)

            //Restore Settings
            RestoreSettings(viewModel::onEvent)

            //Share Settings
            ShareSettings(viewModel::onEvent)
        }
    }

}

/**
 * Save Settings
 * Shows warning Dialog that the file contains sensitive information
 */
@Composable
private fun SaveSettings(onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit) {

    var openSaveSettingsDialog by remember { mutableStateOf(false) }

    //save settings
    ListElement(
        modifier = Modifier.clickable { openSaveSettingsDialog = true },
        icon = {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = MR.strings.save.stable
            )
        },
        text = {
            Text(MR.strings.save.stable)
        },
        secondaryText = {
            Text(MR.strings.saveSettingsText.stable)
        }
    )

    //save settings dialog
    if (openSaveSettingsDialog) {
        SaveSettingsDialog(
            onConfirm = {
                openSaveSettingsDialog = false
                onEvent(ExportSettingsFile)
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
private fun RestoreSettings(onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit) {

    var openRestoreSettingsDialog by remember { mutableStateOf(false) }

    //restore settings
    ListElement(
        modifier = Modifier.clickable { openRestoreSettingsDialog = true },
        icon = {
            Icon(
                imageVector = Icons.Filled.Restore,
                contentDescription = MR.strings.restore.stable
            )
        },
        text = {
            Text(MR.strings.restore.stable)
        },
        secondaryText = {
            Text(MR.strings.restoreSettingsText.stable)
        }
    )

    //restore settings dialog
    if (openRestoreSettingsDialog) {

        RestoreSettingsDialog(
            onConfirm = {
                openRestoreSettingsDialog = false
                onEvent(RestoreSettingsFromFile)
            },
            onDismiss = {
                openRestoreSettingsDialog = false
            }
        )

    }
}

@Composable
private fun ShareSettings(onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit) {

    //restore settings
    ListElement(
        modifier = Modifier.clickable(onClick = { onEvent(ShareSettingsFile) }),
        icon = {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = MR.strings.share.stable
            )
        },
        text = {
            Text(MR.strings.share.stable)
        },
        secondaryText = {
            Text(MR.strings.shareSettingsText.stable)
        }
    )

}

/**
 * dialog to save settings
 */
@Composable
private fun SaveSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onConfirm,
        headline = {
            Text(MR.strings.saveSettings.stable)
        },
        supportingText = {
            Text(
                resource = MR.strings.saveSettingsWarningText.stable,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning.stable
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok.stable)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel.stable)
            }
        }
    )

}

/**
 * dialog to restore settings
 */
@Composable
private fun RestoreSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        headline = {
            Text(MR.strings.restoreSettings.stable)
        },
        supportingText = {
            Text(
                resource = MR.strings.restoreSettingsWarningText.stable,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning.stable
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok.stable)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel.stable)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

}