package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.color_http
import org.rhasspy.mobile.resources.color_local
import org.rhasspy.mobile.resources.color_mqtt
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState.SourceViewState.SourceType.*
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogStateViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogScreen() {
    val viewModel: DialogScreenViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        Scaffold(
            modifier = Modifier
                .testTag(MainScreenNavigationDestination.LogScreen)
                .fillMaxSize(),
            topBar = {
                AppBar()
            },
        ) { paddingValues ->

            Surface(Modifier.padding(paddingValues)) {
                DialogScreenContent(
                    history = viewState.history
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
private fun AppBar() {
    TopAppBar(
        modifier = Modifier,
        title = { Text(MR.strings.dialog.stable) },
    )
}

@Composable
private fun DialogScreenContent(
    history: ImmutableList<DialogTransitionItem>
) {

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {

        items(history) { item ->
            DialogTransitionListItem(item)
        }

    }

}

@Composable
fun DialogTransitionListItem(item: DialogTransitionItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .height(10.dp)
                .width(1.dp)
        )
        DialogActionListItem(item.action)
        Divider(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .height(10.dp)
                .width(1.dp)
        )
        DialogStateListItem(item.state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogActionListItem(item: DialogActionViewState) {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                resource = item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
            )

            val color = when (item.source.type) {
                Http -> MaterialTheme.colorScheme.color_http
                Local -> MaterialTheme.colorScheme.color_local
                MQTT -> MaterialTheme.colorScheme.color_mqtt
            }

            Badge(
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                containerColor = color,
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    resource = item.source.name,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End
                )
            }
        }

        item.information?.also {
            Text(
                resource = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogStateListItem(item: DialogStateViewState) {
    ListElement(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        overlineText = {
            Text(
                item.name
            )
        },
        text = {
            Text(text = "dummx")
        },
        secondaryText = {
            Text("item.time")
        }
    )

}