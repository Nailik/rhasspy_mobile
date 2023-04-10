package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.LogListElement
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.SetSystemColor
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListAutoscroll
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListFiltered

/**
 * screen that's shown when a configuration is being tested
 */
@Composable
fun ConfigurationScreenTest(
    viewState: ConfigurationTestViewState,
    onAction: (IConfigurationTestUiAction) -> Unit,
    content: (@Composable () -> Unit)?
) {
    SetSystemColor(1.dp)

    val navController = LocalConfigurationNavController.current
    val nativeApplication = get<NativeApplication>()

    LaunchedEffect(Unit) {
        nativeApplication.isAppInBackground.collect {
            if (it) {
                navController.popBackStack()
            }
        }
    }

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
    onAction: (IConfigurationTestUiAction) -> Unit,
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
            IconButton(onClick = { onAction(ToggleListAutoscroll) }) {
                Icon(
                    imageVector = if (viewState.isListAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList.stable
                )
            }
        }
    )
}