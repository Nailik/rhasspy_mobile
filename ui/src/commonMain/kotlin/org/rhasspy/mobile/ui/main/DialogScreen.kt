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
import org.rhasspy.mobile.resources.*
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
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
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.getName()
                )

                Badge(
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    containerColor = item.source.getColor(),
                    modifier = Modifier.wrapContentSize()
                ) {

                    Text(
                        text = item.source.getName(),
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
    Source.HomeAssistant      -> MaterialTheme.colorScheme.color_home_assistant
    Source.WebServer          -> MaterialTheme.colorScheme.color_webserver
    Source.User               -> MaterialTheme.colorScheme.color_user
}

private fun Source.getName() = when (this) {
    Source.Local              -> "Local"
    Source.Rhasspy2HermesHttp -> "Rhasspy2HermesHttp"
    Source.Rhasspy2HermesMqtt -> "Rhasspy2HermesMqtt"
    Source.HomeAssistant      -> "HomeAssistant"
    Source.WebServer          -> "WebServer"
    Source.User               -> "User"
}

private fun PipelineEvent.getName() = when (this) {
    is HandleResult.Handle                 -> "Handle"
    is HandleResult.HandleDisabled         -> "HandleDisabled"
    is HandleResult.HandleTimeout          -> "HandleTimeout"
    is HandleResult.NotHandled             -> "NotHandled"
    is IntentResult.Intent                 -> "Intent"
    is IntentResult.IntentDisabled         -> "IntentDisabled"
    is IntentResult.NotRecognized          -> "NotRecognized"
    is PipelineResult.End                  -> "End"
    is TtsResult.NotSynthesized            -> "NotSynthesized"
    is SndResult.NotPlayed                 -> "NotPlayed"
    is SndResult.PlayDisabled              -> "PlayDisabled"
    is SndResult.Played                    -> "Played"
    is TranscriptResult.TranscriptDisabled -> "TranscriptDisabled"
    is TranscriptResult.TranscriptError    -> "TranscriptError"
    is TranscriptResult.TranscriptTimeout  -> "TranscriptTimeout"
    is TtsResult.TtsDisabled               -> "TtsDisabled"
    is TranscriptResult.Transcript         -> "Transcript"
    is TtsResult.Audio                     -> "Audio"
    is VadResult.VoiceEnd.VadDisabled      -> "VadDisabled"
    is VadResult.VoiceEnd.VadTimeout       -> "VadTimeout"
    is VadResult.VoiceEnd.VoiceStopped     -> "VoiceStopped"
    is VadResult.VoiceStart                -> "VoiceStart"
    is WakeResult                          -> "WakeResult"
    is SndResult.SndTimeout                -> "SndTimeout"
    is TtsResult.TtsTimeout                -> "TtsTimeout"
    is SndAudio.AudioChunkEvent            -> "AudioChunkEvent"
    is SndAudio.AudioStartEvent            -> "AudioStartEvent"
    is SndAudio.AudioStopEvent             -> "AudioStopEvent"
}

