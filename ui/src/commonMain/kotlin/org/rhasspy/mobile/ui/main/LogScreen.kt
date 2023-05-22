package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.LogListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.LogScreen
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel

/**
 * show log information
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen() {
    val viewModel: LogScreenViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        Scaffold(
            modifier = Modifier
                .testTag(LogScreen)
                .fillMaxSize(),
            topBar = {
                AppBar(
                    isLogAutoscroll = viewState.isLogAutoscroll,
                    onEvent = viewModel::onEvent
                )
            },
        ) { paddingValues ->

            Surface(Modifier.padding(paddingValues)) {
                LogScreenContent(
                    isLogAutoscroll = viewState.isLogAutoscroll,
                    logList = viewState.logList
                )
            }

        }
    }
}

/**
 * app bar of log screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    isLogAutoscroll: Boolean,
    onEvent: (LogScreenUiEvent) -> Unit
) {
    TopAppBar(modifier = Modifier,
        title = { Text(MR.strings.log.stable) },
        actions = {
            LogScreenActions(
                isLogAutoscroll = isLogAutoscroll,
                onEvent = onEvent
            )
        }
    )
}

/**
 * visible content on log screen
 */
@Composable
private fun LogScreenContent(
    isLogAutoscroll: Boolean,
    logList: ImmutableList<LogElement>
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    if (isLogAutoscroll) {
        LaunchedEffect(logList.size) {
            coroutineScope.launch {
                if (logList.isNotEmpty()) {
                    scrollState.animateScrollToItem(logList.size - 1)
                }
            }
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxHeight()
    ) {
        items(logList) { item ->
            LogListElement(item)
            CustomDivider()
        }
    }

}

/**
 * log screen actions to save and share log file
 */
@Composable
private fun LogScreenActions(
    isLogAutoscroll: Boolean,
    onEvent: (LogScreenUiEvent) -> Unit
) {

    Row(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { onEvent(ToggleListAutoScroll) }) {
            Icon(
                imageVector = if (isLogAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                contentDescription = MR.strings.autoscrollList.stable
            )
        }

        IconButton(onClick = { onEvent(ShareLogFile) }) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = MR.strings.share.stable
            )
        }

        IconButton(onClick = { onEvent(SaveLogFile) }) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = MR.strings.save.stable
            )
        }
    }

}