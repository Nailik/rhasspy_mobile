package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.SaveAndRestoreSettings
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel

/**
 * to save and restore settings
 */

@Composable
fun SaveAndRestoreSettingsContent() {
    val viewModel: SaveAndRestoreSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
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
            SaveSettings(
                isSaveSettingsToFileDialogVisible = viewState.isSaveSettingsToFileDialogVisible,
                onEvent = viewModel::onEvent
            )

            //Restore Settings
            RestoreSettings(
                isRestoreSettingsFromFileDialogVisible = viewState.isRestoreSettingsFromFileDialogVisible,
                onEvent = viewModel::onEvent
            )

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
private fun SaveSettings(
    isSaveSettingsToFileDialogVisible: Boolean,
    onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit
) {

    //save settings
    ListElement(
        modifier = Modifier.clickable { onEvent(ExportSettingsFile) },
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
    if (isSaveSettingsToFileDialogVisible) {
        SaveSettingsDialog(
            onConfirm = { onEvent(ExportSettingsFileDialogResult(true)) },
            onDismiss = { onEvent(ExportSettingsFileDialogResult(false)) }
        )
    }
}

/**
 * Restore settings
 * shows dialog that current settings will be overwritten
 */
@Composable
private fun RestoreSettings(
    isRestoreSettingsFromFileDialogVisible: Boolean,
    onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit
) {

    //restore settings
    ListElement(
        modifier = Modifier.clickable { onEvent(RestoreSettingsFromFile) },
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
    if (isRestoreSettingsFromFileDialogVisible) {

        RestoreSettingsDialog(
            onConfirm = { onEvent(RestoreSettingsFromFileDialogResult(true)) },
            onDismiss = { onEvent(RestoreSettingsFromFileDialogResult(false)) }
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
        testTag = TestTag.DialogSaveSettings,
        icon = Icons.Filled.Warning,
        title = MR.strings.saveSettings.stable,
        message = MR.strings.saveSettingsWarningText.stable,
        confirmLabel = MR.strings.ok.stable,
        dismissLabel = MR.strings.cancel.stable,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )

}

/**
 * dialog to restore settings
 */
@Composable
private fun RestoreSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        testTag = TestTag.DialogRestoreSettings,
        icon = Icons.Filled.Warning,
        title = MR.strings.restoreSettings.stable,
        message = MR.strings.restoreSettingsWarningText.stable,
        confirmLabel = MR.strings.ok.stable,
        dismissLabel = MR.strings.cancel.stable,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )

}