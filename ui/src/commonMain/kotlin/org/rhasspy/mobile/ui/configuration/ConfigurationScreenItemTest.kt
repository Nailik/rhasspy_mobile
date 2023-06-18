package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ServiceStateDialog
import org.rhasspy.mobile.ui.content.ServiceStateHeader
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.LogListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.DialogAction.Confirm
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.DialogAction.Dismiss
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestViewState.DialogState
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestViewState.DialogState.ServiceStateDialog
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

/**
 * screen that's shown when a configuration is being tested
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenTest(
    title: StableStringResource,
    viewState: ConfigurationTestViewState,
    onEvent: (ConfigurationTestUiEvent) -> Unit,
    content: (@Composable () -> Unit)?
) {
    SetSystemColor(1.dp)

    viewState.dialogState?.also {
        Dialogs(
            dialog = it,
            onEvent = onEvent
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                viewState = viewState,
                onEvent = onEvent
            )
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            tonalElevation = 3.dp
        ) {
            ConfigurationScreenTestList(
                viewState = viewState,
                serviceViewState = viewState.serviceViewState,
                isOpenServiceStateDialogEnabled = viewState.isOpenServiceStateDialogEnabled,
                onEvent = onEvent,
                content = content
            )
        }
    }
}

/**
 * list and custom content
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ConfigurationScreenTestList(
    modifier: Modifier = Modifier,
    viewState: ConfigurationTestViewState,
    serviceViewState: ServiceViewState,
    isOpenServiceStateDialogEnabled: Boolean,
    onEvent: (ConfigurationTestUiEvent) -> Unit,
    content: (@Composable () -> Unit)?,
) {
    Column(modifier = modifier) {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logEventsList by viewState.logEvents.collectAsState()

        if (viewState.isListAutoscroll) {
            LaunchedEffect(logEventsList.size) {
                coroutineScope.launch {
                    if (logEventsList.isNotEmpty()) {
                        scrollState.animateScrollToItem(logEventsList.size - 1)
                    }
                }
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            stickyHeader {
                ServiceStateHeader(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    serviceViewState = serviceViewState,
                    enabled = isOpenServiceStateDialogEnabled,
                    onClick = { onEvent(OpenServiceStateDialog) }
                )
            }

            items(logEventsList) { item ->
                LogListElement(item)
                CustomDivider()
            }
        }

        if (content != null) {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 0.dp,
            ) {
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
    dialog: DialogState,
    onEvent: (ConfigurationTestUiEvent) -> Unit
) {
    when (dialog) {
        is ServiceStateDialog -> {
            ServiceStateDialog(
                dialogText = dialog.dialogText,
                onConfirm = { onEvent(Confirm) },
                onDismiss = { onEvent(Dismiss) }
            )
        }
    }
}

/**
 * top app bar with title and back navigation button
 *
 * actions: autoscroll, expand and filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    title: StableStringResource,
    viewState: ConfigurationTestViewState,
    onEvent: (ConfigurationTestUiEvent) -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
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
                        imageVector = Icons.Filled.Close,
                        contentDescription = MR.strings.stop.stable,
                    )
                }
            )
        },
        actions = {
            IconButton(onClick = { onEvent(ToggleListFiltered) }) {
                Icon(
                    imageVector = if (viewState.isListFiltered) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = MR.strings.filterList.stable
                )
            }
            IconButton(onClick = { onEvent(ToggleListAutoscroll) }) {
                Icon(
                    imageVector = if (viewState.isListAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList.stable
                )
            }
        }
    )
}