package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.format
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.color_http
import org.rhasspy.mobile.resources.color_local
import org.rhasspy.mobile.resources.color_mqtt
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.utils.ListType.DialogScreenList
import org.rhasspy.mobile.ui.utils.rememberForeverLazyListState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.DialogScreen
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogInformationItem
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogInformationItem.DialogActionViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogInformationItem.DialogActionViewState.SourceViewState.SourceType.*
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogInformationItem.DialogStateViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewModel

@Composable
fun DialogScreen() {
    val viewModel: DialogScreenViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        Scaffold(
            modifier = Modifier
                .testTag(DialogScreen)
                .fillMaxSize(),
            topBar = {
                AppBar(
                    isLogAutoscroll = viewState.isDialogAutoscroll,
                    onEvent = viewModel::onEvent
                )
            },
        ) { paddingValues ->

            Surface(Modifier.padding(paddingValues)) {
                DialogScreenContent(
                    isLogAutoscroll = viewState.isDialogAutoscroll,
                    history = viewState.history,
                    onEvent = viewModel::onEvent
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
    onEvent: (DialogScreenUiEvent) -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = { Text(MR.strings.dialog.stable) },
        actions = {
            IconButton(onClick = { onEvent(ToggleListAutoScroll) }) {
                Icon(
                    imageVector = if (isLogAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList.stable
                )
            }
            IconButton(onClick = { onEvent(ClearHistory) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.clear_text.stable
                )
            }
        }
    )
}

@Composable
private fun DialogScreenContent(
    isLogAutoscroll: Boolean,
    history: ImmutableList<DialogInformationItem>,
    onEvent: (DialogScreenUiEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberForeverLazyListState(DialogScreenList)

    if (isLogAutoscroll) {
        LaunchedEffect(history.size) {
            coroutineScope.launch {
                if (history.isNotEmpty()) {
                    lazyListState.animateScrollToItem(history.size - 1)
                }
            }
        }
    }

    val isDraggedState by lazyListState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isDraggedState) {
        if (isDraggedState) {
            onEvent(ManualListScroll)
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        state = lazyListState
    ) {

        items(history) { item ->
            DialogTransitionListItem(item)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}

@Composable
fun DialogTransitionListItem(item: DialogInformationItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Divider(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .height(10.dp)
                .width(1.dp)
        )

        when (item) {
            is DialogActionViewState -> DialogActionListItem(item)
            is DialogStateViewState  -> DialogStateListItem(item)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogActionListItem(item: DialogActionViewState) {
    ListElement(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(5.dp)
        ),
        text = {
            Row {
                Text(
                    resource = item.name,
                    modifier = Modifier.weight(1f)
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
        },
        secondaryText = item.information?.let {
            {
                Text(
                    resource = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        overlineText = {
            Text(item.timeStamp.toLocalDateTime(currentSystemDefault()).toString())
        }
    )
}

@Composable
private fun DialogStateListItem(item: DialogStateViewState) {
    ListElement(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        text = { Text(item.name) },
        secondaryText = item.sessionData?.let { { DialogSessionData(it) } },
        overlineText = { Text(item.timeStamp.toLocalDateTime(currentSystemDefault()).toString()) }
    )
}

@Composable
private fun DialogSessionData(sessionData: SessionData) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(MR.strings.session_id.format(sessionData.sessionId).stable)
        Text(MR.strings.wake_word.format(sessionData.wakeWord.toString()).stable)
        Text(MR.strings.send_audio_captured.format(sessionData.sendAudioCaptured.toString()).stable)
        Text(MR.strings.asr_text.format(sessionData.recognizedText.toString()).stable)
    }
}