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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.color_http
import org.rhasspy.mobile.resources.color_local
import org.rhasspy.mobile.resources.color_mqtt
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.theme.TonalElevationLevel0
import org.rhasspy.mobile.ui.utils.ListType.DialogScreenList
import org.rhasspy.mobile.ui.utils.rememberForeverLazyListState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewModel

@Composable
fun DialogScreen(viewModel: DialogScreenViewModel) {

    val viewState by viewModel.viewState.collectAsState()

    ScreenContent(
        title = MR.strings.dialog_pipeline.stable,
        actions = {
            IconButton(onClick = { viewModel.onEvent(ToggleListAutoScroll) }) {
                Icon(
                    imageVector = if (viewState.isDialogAutoscroll) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.autoscrollList.stable
                )
            }
            IconButton(onClick = { viewModel.onEvent(ClearHistory) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.clear_text.stable
                )
            }
        },
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel0,
    ) {

        DialogScreenContent(
            isLogAutoscroll = viewState.isDialogAutoscroll,
            history = viewState.history,
            onEvent = viewModel::onEvent,
        )

    }


}

@Composable
private fun DialogScreenContent(
    isLogAutoscroll: Boolean,
    history: StateFlow<ImmutableList<PipelineEvent>>,
    onEvent: (DialogScreenUiEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberForeverLazyListState(DialogScreenList)

    val historyList by history.collectAsState()

    if (isLogAutoscroll) {
        LaunchedEffect(historyList.size) {
            coroutineScope.launch {
                if (historyList.isNotEmpty()) {
                    lazyListState.animateScrollToItem(historyList.size - 1)
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

        items(historyList) { item ->
            PipelineEventItem(item)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PipelineEventItem(item: PipelineEvent) {
    ListElement(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(5.dp)
        ),
        text = {
            Row {
                Text(item.getName())

                Badge(
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    containerColor = item.source.getColor(),
                    modifier = Modifier.wrapContentSize()
                ) {

                    Text(
                        resource = item.source.getName(),
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End
                    )

                }
            }
        },
        secondaryText = null, //TODO #466 information
        overlineText = {
            Text(item.timeStamp.toLocalDateTime(currentSystemDefault()).toString())
        }
    )
}


@Composable
private fun Source.getColor() = when (this) {
    Source.Local              -> MaterialTheme.colorScheme.color_local
    Source.Rhasspy2HermesHttp -> MaterialTheme.colorScheme.color_http
    Source.Rhasspy2HermesMqtt -> MaterialTheme.colorScheme.color_mqtt
    Source.HomeAssistant      -> TODO()
    Source.WebServer          -> TODO()
    Source.User               -> TODO()
}

private fun Source.getName() = when (this) {
    Source.Local              -> MR.strings.local.stable
    Source.Rhasspy2HermesHttp -> TODO()
    Source.Rhasspy2HermesMqtt -> TODO()
    Source.HomeAssistant      -> TODO()
    Source.WebServer          -> TODO()
    Source.User               -> TODO()
}

private fun PipelineEvent.getName() = when (this) {
    is HandleResult.Handle                 -> MR.strings.handleWithRecognition.stable
    is HandleResult.HandleDisabled         -> TODO()
    is HandleResult.HandleTimeout          -> TODO()
    is HandleResult.NotHandled             -> TODO()
    is IntentResult.Intent                 -> TODO()
    is IntentResult.IntentDisabled         -> TODO()
    is IntentResult.NotRecognized          -> TODO()
    is PipelineResult.End                  -> TODO()
    is TtsResult.NotSynthesized            -> TODO()
    is SndResult.NotPlayed                 -> TODO()
    is SndResult.PlayDisabled              -> TODO()
    is SndResult.Played                    -> TODO()
    is TranscriptResult.TranscriptDisabled -> TODO()
    is TranscriptResult.TranscriptError    -> TODO()
    is TranscriptResult.TranscriptTimeout  -> TODO()
    is TtsResult.TtsDisabled               -> TODO()
    is TranscriptResult.Transcript         -> TODO()
    is TtsResult.Audio                     -> TODO()
    is VadResult.VoiceEnd.VadDisabled      -> TODO()
    is VadResult.VoiceEnd.VadTimeout       -> TODO()
    is VadResult.VoiceEnd.VoiceStopped     -> TODO()
    is VadResult.VoiceStart                -> TODO()
    is WakeResult                          -> TODO()
}

