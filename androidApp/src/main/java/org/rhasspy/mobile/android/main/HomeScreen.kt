package org.rhasspy.mobile.android.main

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextWithAction
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

var isMainActionBig = mutableStateOf(true)
var mainActionVisible = mutableStateOf(true)


@Preview
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    HomeScreenContent(viewModel)
}


/**
 * microphone button and bottom actions
 */
@Composable
private fun HomeScreenContent(viewModel: HomeScreenViewModel) {

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                WakeUpAction(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    viewModel = viewModel,
                )
                BottomActions(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    viewModel = viewModel
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                WakeUpAction(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    viewModel = viewModel
                )
                BottomActions(
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel
                )
            }

        }
    }

}

/**
 * Microphone button
 */
@Composable
fun WakeUpAction(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel) {
    //make smaller according to
    BoxWithConstraints(
        modifier = modifier.padding(Dp(24f))
    ) {
        isMainActionBig.value = maxHeight >= 96.dp + (24.dp * 2)
        mainActionVisible.value = maxHeight > 24.dp || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        AnimatedVisibility(
            enter = expandIn(expandFrom = Alignment.TopEnd),
            exit = shrinkOut(shrinkTowards = Alignment.Center),
            visible = mainActionVisible.value,
            modifier = Modifier.fillMaxSize()
        ) {
            MainActionFab(modifier = Modifier.fillMaxSize(), mainActionVisible.value, viewModel)
        }
    }
}

/**
 * Big microphone floating action button
 */
@Composable
fun MainActionFab(modifier: Modifier = Modifier, isMainActionBig: Boolean, viewModel: HomeScreenViewModel) {
    val iconSize = animateDpAsState(targetValue = if (isMainActionBig) 96.dp else 24.dp)

    Fab(modifier = modifier, iconSize = iconSize.value, viewModel)

}

/**
 * Bottom actions contain text to recognize and text to speak
 */
@Composable
fun BottomActions(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel) {
    Column(
        modifier = modifier
            .padding(bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val childModifier = when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> Modifier
            else -> Modifier
                .fillMaxWidth()
                .padding(start = 24.dp)
        }

        TopRow(viewModel)
        TextToRecognize(childModifier, viewModel)
        TextToSpeak(childModifier, viewModel)
    }
}

/**
 * shows microphone button when in portrait mode and keyboard is shown
 */
@Composable
fun TopRow(viewModel: HomeScreenViewModel) {
    val state = animateDpAsState(targetValue = if (!mainActionVisible.value) 12.dp else 0.dp)

    Row(
        modifier = Modifier
            .padding(start = state.value),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        AnimatedVisibility(
            enter = expandIn(expandFrom = Alignment.BottomStart),
            exit = shrinkOut(shrinkTowards = Alignment.BottomStart),
            visible = !mainActionVisible.value
        ) {
            MainActionFab(isMainActionBig = mainActionVisible.value, viewModel = viewModel)
        }

        PlayRecording(
            modifier = Modifier.padding(horizontal = state.value),
            viewModel = viewModel
        )
    }
}

/**
 * button to play latest recording
 */
@Composable
fun PlayRecording(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel
) {
    val isPlaying = false//viewModel.currentState.collectAsState().value == State.PlayingRecording

    ElevatedButton(
        onClick = viewModel::togglePlayRecording,
        modifier = modifier
    ) {
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.Stop,
                contentDescription = MR.strings.playRecording
            )
        }
        Text(resource = if (isPlaying) MR.strings.stopPlayRecording else MR.strings.playRecording)
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = MR.strings.playRecording
            )
        }
    }
}

/**
 * action to send text to recognize
 */
@Composable
fun TextToRecognize(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel
) {
    var textToRecognize by rememberSaveable { mutableStateOf("") }

    TextWithAction(
        modifier = modifier,
        label = MR.strings.textToRecognize,
        text = textToRecognize,
        onValueChange = { textToRecognize = it },
        onClick = { viewModel.intentRecognition(textToRecognize) }) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = MR.strings.textToRecognize,
        )
    }
}

/**
 * text field to post text to speak
 */
@Composable
fun TextToSpeak(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel
) {
    var textToSpeak by rememberSaveable { mutableStateOf("") }

    TextWithAction(
        modifier = modifier,
        label = MR.strings.textToSpeak,
        text = textToSpeak,
        onValueChange = { textToSpeak = it },
        onClick = { viewModel.speakText(textToSpeak) }) {
        Icon(
            imageVector = Icons.Filled.VolumeUp,
            contentDescription = MR.strings.textToSpeak,
        )
    }
}


@Composable
fun MicrophonePermissionMissing(viewModel: HomeScreenViewModel) {

    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = !viewModel.isMicrophonePermissionRequestNotRequired.collectAsState().value
    ) {

        IconButton(
            onClick = { },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            Icon(
                imageVector = Icons.Filled.MicOff,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                contentDescription = MR.strings.microphone
            )
        }

    }

}

@Composable
fun OverlayPermissionMissing(viewModel: HomeScreenViewModel) {

    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = viewModel.isOverlayPermissionRequestRequired.collectAsState().value
    ) {


        IconButton(
            onClick = { },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.LayersClear,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                contentDescription = MR.strings.overlay
            )
        }

    }

}


@Composable
fun MqttConnectionStatus() {

    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = /*ConfigurationSettings.isMqttEnabled.data.collectAsState().value && */!MqttService.isConnected.collectAsState().value
    ) {

        var openDialog by rememberSaveable { mutableStateOf(false) }

        IconButton(
            onClick = { openDialog = true },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = MR.strings.notConnected
            )
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                confirmButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text(MR.strings.ok)
                    }
                },
                title = {
                    Text(MR.strings.mqttNotConnected)
                },
                text = {
                    MqttService.hasConnectionError.collectAsState().value?.also {
                        val text = translate(MR.strings.mqttConnectionError)
                        Text("$text\n${it.msg} (${it.statusCode})")
                    }

                    MqttService.hasConnectionLostError.collectAsState().value?.also {
                        val text = translate(MR.strings.mqttConnectionLostError)
                        Text("$text\n${it.message}")
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                properties = DialogProperties(usePlatformDefaultWidth = false)
            )
        }

    }

}
