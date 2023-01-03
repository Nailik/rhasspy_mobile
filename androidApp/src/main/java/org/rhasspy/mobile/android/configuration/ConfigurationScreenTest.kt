package org.rhasspy.mobile.android.configuration

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
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.ServiceState
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.LogListElement
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

/**
 * screen that's shown when a configuration is being tested
 */
@Composable
fun ConfigurationScreenTest(
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?,
    onOpenPage: () -> Unit
) {
    SetSystemColor(1.dp)

    val navController = LocalConfigurationNavController.current

    LaunchedEffect(Unit) {
        onOpenPage.invoke()
        Application.nativeInstance.isAppInBackground.collect {
            if (it) {
                navController.popBackStack()
            }
        }
    }

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
        Surface(
            modifier = Modifier.padding(paddingValues),
            tonalElevation = 3.dp
        ) {
            ConfigurationScreenTestList(
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
    modifier: Modifier = Modifier,
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?
) {
    Column(modifier = modifier) {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logEventsList by viewModel.logEvents.collectAsState()

        LaunchedEffect(logEventsList.size) {
            coroutineScope.launch {
                if (logEventsList.isNotEmpty()) {
                    scrollState.animateScrollToItem(logEventsList.size - 1)
                }
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            stickyHeader {
                ServiceState(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .padding(16.dp),
                    serviceState = viewModel.serviceState.collectAsState().value
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
            IconButton(onClick = viewModel::toggleListFiltered) {
                Icon(
                    imageVector = if (viewModel.isListFiltered.collectAsState().value) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = MR.strings.filterList
                )
            }
            IconButton(onClick = viewModel::toggleListAutoscroll) {
                Icon(
                    imageVector = if (viewModel.isListAutoscroll.collectAsState().value) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList
                )
            }
        }
    )
}