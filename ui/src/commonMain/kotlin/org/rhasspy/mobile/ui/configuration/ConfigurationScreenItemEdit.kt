package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ServiceStateDialog
import org.rhasspy.mobile.ui.content.ServiceStateHeader
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.ServiceStateDialogState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction.*


/**
 * configuration screen where settings are edited
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenItemEdit(
    title: StableStringResource,
    viewState: ConfigurationEditViewState,
    onEvent: (ConfigurationEditUiEvent) -> Unit,
    content: LazyListScope.() -> Unit
) {
    SetSystemColor(0.dp)

    viewState.dialogState?.also {
        Dialogs(
            dialogState = it,
            onEvent = onEvent
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                onEvent = onEvent
            )
        },
        bottomBar = {
            BottomAppBar(
                hasUnsavedChanges = viewState.hasUnsavedChanges,
                isTestingEnabled = viewState.isTestingEnabled,
                onAction = { onEvent(it) },
            )
        }
    ) { paddingValues ->
        Surface(tonalElevation = 1.dp) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                stickyHeader {
                    ServiceStateHeader(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        serviceViewState = viewState.serviceViewState,
                        enabled = viewState.isOpenServiceStateDialogEnabled,
                        onClick = { onEvent(OpenServiceStateDialog) }
                    )
                }

                content()
            }
        }
    }

}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun Dialogs(
    dialogState: DialogState,
    onEvent: (ConfigurationEditUiEvent) -> Unit
) {
    when (dialogState) {
        is ServiceStateDialogState -> {
            ServiceStateDialog(
                dialogText = dialogState.dialogText,
                onConfirm = { onEvent(Confirm) },
                onDismiss = { onEvent(Dismiss) }
            )
        }


        UnsavedChangesDialogState -> {
            Dialog(
                icon = Icons.Filled.Warning,
                title = MR.strings.unsavedChanges.stable,
                message = MR.strings.unsavedChangesInformation.stable,
                confirmLabel = MR.strings.save.stable,
                dismissLabel = MR.strings.discard.stable,
                onConfirm = { onEvent(Confirm) },
                onDismiss = { onEvent(Dismiss) },
                onClose = { onEvent(Close) }
            )
        }
    }
}

/**
 * bottom app bar
 * discard, save actions
 * fab for testing
 */
@Composable
private fun BottomAppBar(
    hasUnsavedChanges: Boolean,
    isTestingEnabled: Boolean,
    onAction: (ConfigurationEditUiEvent) -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = { onAction(Discard) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Delete else Icons.Filled.Delete,
                    contentDescription = MR.strings.discard.stable,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = { onAction(Save) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Save else Icons.Filled.Save,
                    contentDescription = MR.strings.save.stable
                )
            }
        },
        floatingActionButton = {
            FloatingActionButtonElement(
                hasUnsavedChanges = hasUnsavedChanges,
                isTestingEnabled = isTestingEnabled,
                onAction = onAction
            )
        }
    )
}

@Composable
private fun FloatingActionButtonElement(
    hasUnsavedChanges: Boolean,
    isTestingEnabled: Boolean,
    onAction: (ConfigurationEditUiEvent) -> Unit
) {
    org.rhasspy.mobile.ui.content.elements.FloatingActionButton(
        modifier = Modifier
            .testTag(TestTag.BottomAppBarTest)
            .defaultMinSize(
                minWidth = 56.0.dp,
                minHeight = 56.0.dp,
            ),
        onClick = { onAction(OpenTestScreen) },
        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
        contentColor = LocalContentColor.current,
        isEnabled = !hasUnsavedChanges && isTestingEnabled,
        icon = {
            Icon(
                imageVector = if (!hasUnsavedChanges && isTestingEnabled) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                contentDescription = MR.strings.test.stable
            )
        }
    )
}

/**
 * top app bar with title and back navigation button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    title: StableStringResource,
    onEvent: (ConfigurationEditUiEvent) -> Unit
) {

    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)
        ),
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onEvent(BackClick) },
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = MR.strings.back.stable,
                    )
                }
            )
        }
    )
}