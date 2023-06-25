package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ServiceStateDialog
import org.rhasspy.mobile.ui.content.ServiceStateHeader
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.DialogAction.*
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.DialogState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.DialogState.ServiceStateDialogState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel

/**
 * configuration screen where settings are edited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenItemEdit(
    modifier: Modifier,
    kViewModel: IScreenViewModel,
    title: StableStringResource,
    viewState: IConfigurationViewState,
    onEvent: (IConfigurationUiEvent) -> Unit,
    content: @Composable () -> Unit
) {

    Screen(
        modifier = modifier,
        screenViewModel = kViewModel
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
                    onEvent = onEvent,
                )
            }
        ) { paddingValues ->

            Surface(tonalElevation = 1.dp) {

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {

                    ServiceStateHeader(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        serviceViewState = viewState.serviceViewState,
                        enabled = viewState.isOpenServiceStateDialogEnabled,
                        onClick = { onEvent(OpenServiceStateDialog) }
                    )

                    content()
                }
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
    onEvent: (IConfigurationUiEvent) -> Unit
) {
    when (dialogState) {
        is ServiceStateDialogState -> {
            ServiceStateDialog(
                dialogText = dialogState.dialogText,
                onConfirm = { onEvent(Confirm(dialogState)) },
                onDismiss = { onEvent(Dismiss(dialogState)) }
            )
        }


        UnsavedChangesDialogState -> {
            Dialog(
                testTag = TestTag.DialogUnsavedChanges,
                icon = Icons.Filled.Warning,
                title = MR.strings.unsavedChanges.stable,
                message = MR.strings.unsavedChangesInformation.stable,
                confirmLabel = MR.strings.save.stable,
                dismissLabel = MR.strings.discard.stable,
                onConfirm = { onEvent(Confirm(dialogState)) },
                onDismiss = { onEvent(Dismiss(dialogState)) },
                onClose = { onEvent(Close(dialogState)) }
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
    onEvent: (IConfigurationUiEvent) -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = { onEvent(Discard) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Delete else Icons.Filled.Delete,
                    contentDescription = MR.strings.discard.stable,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = { onEvent(Save) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Save else Icons.Filled.Save,
                    contentDescription = MR.strings.save.stable
                )
            }
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
    onEvent: (IConfigurationUiEvent) -> Unit
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