package org.rhasspy.mobile.android.configuration

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
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.android.content.ServiceStateHeader
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.android.content.list.LogListElement
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.SetSystemColor
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.ToggleListFiltered

/**
 * screen that's shown when a configuration is being tested
 */
@Composable
fun ConfigurationScreenTest(
    viewState: ConfigurationTestViewState,
    onAction: (IConfigurationUiEvent) -> Unit,
    content: (@Composable () -> Unit)?
) {
    SetSystemColor(1.dp)

    val navController = LocalConfigurationNavController.current

    Scaffold(
        topBar = {
            AppBar(
                viewState = viewState,
                onAction = onAction,
                title = MR.strings.test.stable,
                onBackClick = navController::popBackStack
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = MR.strings.stop.stable,
                )
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            tonalElevation = 3.dp
        ) {
            ConfigurationScreenTestList(
                viewState = viewState,
                content = content
            )
        }
    }
}

/**
 * list and custom content
 */
@Composable
private fun ConfigurationScreenTestList(
    modifier: Modifier = Modifier,
    viewState: ConfigurationTestViewState,
    content: (@Composable () -> Unit)?
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
                ServiceStateHeader(viewState.serviceViewState.collectAsState().value)
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
 * top app bar with title and back navigation button
 *
 * actions: autoscroll, expand and filter
 */
@Composable
private fun AppBar(
    viewState: ConfigurationTestViewState,
    onAction: (IConfigurationUiEvent) -> Unit,
    title: StableStringResource,
    onBackClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        ),
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = icon
            )
        },
        actions = {
            IconButton(onClick = { onAction(ToggleListFiltered) }) {
                Icon(
                    imageVector = if (viewState.isListFiltered) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = MR.strings.filterList.stable
                )
            }
            IconButton(onClick = { onAction(IConfigurationUiEvent.Action.ToggleListAutoscroll) }) {
                Icon(
                    imageVector = if (viewState.isListAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList.stable
                )
            }
        }
    )
}