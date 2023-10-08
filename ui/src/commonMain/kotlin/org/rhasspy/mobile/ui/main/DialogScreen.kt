package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.PipelineResult.PipelineErrorResult
import org.rhasspy.mobile.resources.*
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
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
    history: ImmutableList<DomainResult>,
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
        modifier = Modifier.fillMaxSize(),
        state = lazyListState
    ) {

        items(history) { item ->
            PipelineEventItem(item)
            CustomDivider()
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PipelineEventItem(item: DomainResult) {
    ListElement(
        overlineText = {
            Text(item.timeStamp.toLocalDateTime(currentSystemDefault()).toString())
        },
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
        secondaryText = {
            InformationText(item)
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

@Composable
private fun InformationText(domainResult: DomainResult) {
    with(domainResult) {
        when (this) {
            is Handle                          -> {
                Text("text: $text\nvolume: $volume")
            }

            is PipelineErrorResult             -> {
                with(reason) {
                    when (this) {
                        is Reason.Disabled -> Text("Disabled")
                        is Reason.Error    -> Text(wrapper = information)
                        is Reason.Timeout  -> Text("Timeout")
                    }
                }

            }

            is IntentResult.Intent             ->
                Text("intentName: $intentName\nintent: $intent")

            is PipelineResult.End              -> {}
            is SndResult.Played                -> {}
            is SndAudio.AudioChunkEvent        -> {}
            is SndAudio.AudioStartEvent        ->
                Text("sampleRate: $sampleRate\nbitRate: $bitRate\nchannel: $channel")

            is SndAudio.AudioStopEvent         -> {}
            is TranscriptResult.Transcript     ->
                Text("text: $text")

            is TtsResult.Audio                 -> {}
            is VadResult.VoiceEnd.VoiceStopped -> {}
            is VadResult.VoiceStart            -> {}
            is WakeResult                      ->
                Text("name: $name")
        }
    }
}

//TODO #466 information
private fun Source.getName() = when (this) {
    Source.Local              -> "Local"
    Source.Rhasspy2HermesHttp -> "Rhasspy2HermesHttp"
    Source.Rhasspy2HermesMqtt -> "Rhasspy2HermesMqtt"
    Source.HomeAssistant      -> "HomeAssistant"
    Source.WebServer          -> "WebServer"
    Source.User               -> "User"
}

//TODO #466 information
private fun DomainResult.getName() = when (this) {
    is Handle -> "Handle"
    is HandleResult.HandleError         -> "NotHandled"
    is IntentResult.Intent              -> "Intent"
    is IntentResult.IntentError         -> "NotRecognized"
    is PipelineResult.End               -> "End"
    is TtsResult.TtsError               -> "NotSynthesized"
    is SndResult.SndError               -> "NotPlayed"
    is SndResult.Played                 -> "Played"
    is TranscriptResult.TranscriptError -> "TranscriptError"
    is TranscriptResult.Transcript      -> "Transcript"
    is TtsResult.Audio                  -> "Audio"
    is VadResult.VoiceEnd.VadError      -> "VadDisabled"
    is VadResult.VoiceEnd.VoiceStopped  -> "VoiceStopped"
    is VadResult.VoiceStart             -> "VoiceStart"
    is WakeResult                       -> "WakeResult"
    is SndAudio.AudioChunkEvent         -> "AudioChunkEvent"
    is SndAudio.AudioStartEvent         -> "AudioStartEvent"
    is SndAudio.AudioStopEvent          -> "AudioStopEvent"
}

