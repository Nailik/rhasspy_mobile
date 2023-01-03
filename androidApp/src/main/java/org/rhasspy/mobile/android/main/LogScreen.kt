package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.LogListElement
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.viewmodel.screens.LogScreenViewModel

/**
 * show log information
 */
@Preview
@Composable
fun LogScreen(viewModel: LogScreenViewModel = get()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(viewModel) },
    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {
            LogScreenContent(viewModel)
        }

    }
}

/**
 * app bar of log screen
 */
@Composable
private fun AppBar(viewModel: LogScreenViewModel) {
    TopAppBar(modifier = Modifier,
        title = { Text(MR.strings.log) },
        actions = {
            LogScreenActions(viewModel)
        }
    )
}

/**
 * visible content on log screen
 */
@Composable
private fun LogScreenContent(viewModel: LogScreenViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val items by viewModel.logArr.collectAsState()

    if (viewModel.isListAutoscroll.collectAsState().value) {
        LaunchedEffect(items.size) {
            coroutineScope.launch {
                if (items.isNotEmpty()) {
                    scrollState.animateScrollToItem(items.size - 1)
                }
            }
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxHeight()
    ) {
        items(items) { item ->
            LogListElement(item)
            CustomDivider()
        }
    }

}

/**
 * log screen actions to save and share log file
 */
@Composable
private fun LogScreenActions(viewModel: LogScreenViewModel) {

    Row(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = viewModel::toggleListAutoscroll) {
            Icon(
                imageVector = if (viewModel.isListAutoscroll.collectAsState().value) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                contentDescription = MR.strings.autoscrollList
            )
        }

        IconButton(onClick = viewModel::shareLogFile) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = MR.strings.share)
        }

        IconButton(onClick = viewModel::saveLogFile) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = MR.strings.save)
        }
    }

}