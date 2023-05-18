package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.content.ServiceStateHeader
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.FloatingActionButton
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Edit
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Test

/**
 * Content of Configuration Screen Item
 *
 * AppBar with Back button and title
 * BottomBar with Save, Discard actions and test FAB
 *
 * Shows dialog on Back press when there are unsaved changes
 */
@Composable
fun <V : IConfigurationEditViewState> ConfigurationScreenItemContent(
    modifier: Modifier,
    screenType: ConfigurationScreenDestinationType,
    viewState: ConfigurationViewState<V>,
    onAction: (IConfigurationUiEvent) -> Unit,
    config: ConfigurationScreenConfig = ConfigurationScreenConfig(MR.strings.save.stable),
    testContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {

    Box(modifier = modifier) {
        when (screenType) {
            Edit ->
                EditConfigurationScreen(
                    title = config.title,
                    viewState = viewState.editViewState.collectAsState().value,
                    serviceStateHeaderViewState = viewState.serviceViewState.collectAsState().value,
                    hasUnsavedChanges = viewState.hasUnsavedChanges,
                    showUnsavedChangesDialog = viewState.showUnsavedChangesDialog,
                    onAction = onAction,
                    content = content
                )

            Test ->
                ConfigurationScreenTest(
                    viewState = viewState.testViewState.collectAsState().value,
                    onAction = onAction,
                    content = testContent
                )
        }
    }

}

/**
 * configuration screen where settings are edited
 */
@Composable
private fun EditConfigurationScreen(
    title: StableStringResource,
    viewState: IConfigurationEditViewState,
    serviceStateHeaderViewState: ServiceStateHeaderViewState,
    hasUnsavedChanges: Boolean,
    showUnsavedChangesDialog: Boolean,
    onAction: (IConfigurationUiEvent) -> Unit,
    content: LazyListScope.() -> Unit
) {
    SetSystemColor(0.dp)

    Box {

        Scaffold(
            topBar = {
                AppBar(
                    title = title,
                    onBackClick = {
                        onAction(BackPress)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = MR.strings.back.stable,
                    )
                }
            },
            bottomBar = {
                BottomAppBar(
                    hasUnsavedChanges = hasUnsavedChanges,
                    isTestingEnabled = viewState.isTestingEnabled,
                    onAction = { onAction(it) },
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
                        ServiceStateHeader(serviceStateHeaderViewState)
                    }

                    content()
                }
            }
        }


        //Show unsaved changes dialog back press
        if (showUnsavedChangesDialog) {
            UnsavedBackButtonDialog(
                onSave = { onAction(SaveDialog) },
                onDiscard = { onAction(DiscardDialog) },
                onClose = { onAction(DismissDialog) }
            )
        }

    }

}


/**
 * unsaved dialog on back button
 */
@Composable
private fun UnsavedBackButtonDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onClose: () -> Unit
) {
    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = { onSave.invoke() },
        onDiscard = { onDiscard.invoke() },
        dismissButtonText = MR.strings.discard.stable
    )
}

@Preview
@Composable
private fun UnsavedChangesDialogPreview() {
    UnsavedChangesDialog(
        onDismissRequest = {},
        onSave = { },
        onDiscard = { },
        dismissButtonText = MR.strings.discard.stable
    )
}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun UnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    dismissButtonText: StableStringResource
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onSave,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.save.stable)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard,
                modifier = Modifier.testTag(TestTag.DialogCancel)
            ) {
                Text(dismissButtonText)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.discard.stable
            )
        },
        headline = { Text(MR.strings.unsavedChanges.stable) },
        supportingText = {
            Text(
                resource = MR.strings.unsavedChangesInformation.stable,
                modifier = Modifier.testTag(TestTag.DialogUnsavedChanges)
            )
        }
    )

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
    onAction: (IConfigurationUiEvent) -> Unit,
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
                onAction = { onAction(it) }
            )
        }
    )
}

@Composable
private fun FloatingActionButtonElement(
    hasUnsavedChanges: Boolean,
    isTestingEnabled: Boolean,
    onAction: (IConfigurationUiEvent) -> Unit
) {
    FloatingActionButton(
        modifier = Modifier
            .testTag(TestTag.BottomAppBarTest)
            .defaultMinSize(
                minWidth = 56.0.dp,
                minHeight = 56.0.dp,
            ),
        onClick = { onAction(StartTest) },
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
@Composable
private fun AppBar(title: StableStringResource, onBackClick: () -> Unit, icon: @Composable () -> Unit) {

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
                onClick = { onBackClick() },
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = icon
            )
        }
    )
}