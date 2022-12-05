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
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.test.EventListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.configuration.IConfigurationViewModel

@Composable
fun ConfigurationScreenTest(
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?,
    onOpenPage: () -> Unit
) {

    val navController = LocalConfigurationNavController.current

    LaunchedEffect(Unit) {
        onOpenPage.invoke()
        Application.Instance.isAppInBackground.collect {
            if (it) {
                navController.popBackStack()
            }
        }
    }

    Surface(tonalElevation = 3.dp) {
        Scaffold(
            topBar = {
                AppBar(
                    viewModel = viewModel,
                    title = MR.strings.test,
                    onBackClick = navController::popBackStack
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = MR.strings.stop,
                    )
                }
            },
        ) { paddingValues ->
            ConfigurationScreenTestList(
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel,
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
    modifier: Modifier,
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?
) {
    Column(modifier = modifier) {
        val eventsList by viewModel.events.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()

        LaunchedEffect(eventsList.size) {
            coroutineScope.launch {
                if (eventsList.isNotEmpty()) {
                    scrollState.animateScrollToItem(eventsList.size - 1)
                }
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            items(eventsList) { item ->
                EventListItem(item)
            }
        }

        if (content != null) {
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
    viewModel: IConfigurationViewModel,
    title: StringResource,
    onBackClick: () -> Unit,
    icon: @Composable () -> Unit
) {

    TopAppBar(
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
            IconButton(onClick = viewModel::toggleListExpanded) {
                Icon(
                    imageVector = if (viewModel.isListExpanded.collectAsState().value) Icons.Filled.Compress else Icons.Filled.Expand,
                    contentDescription = MR.strings.share
                )
            }
            IconButton(onClick = viewModel::toggleListFiltered) {
                Icon(
                    imageVector = if (viewModel.isListFiltered.collectAsState().value) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = MR.strings.share
                ) //FilterListOff
            }
            IconButton(onClick = viewModel::toggleListAutoscroll) {
                Icon(
                    imageVector = if (viewModel.isListAutoscroll.collectAsState().value) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.share
                ) //LowPriority
            }
        }
    )
}