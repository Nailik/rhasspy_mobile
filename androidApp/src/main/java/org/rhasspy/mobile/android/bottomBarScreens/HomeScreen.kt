package org.rhasspy.mobile.android.bottomBarScreens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.navigation.LocalSnackbarHostState
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextWithAction
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

var isMainActionBig = mutableStateOf(true)
var mainActionVisible = mutableStateOf(true)

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {

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

@Composable
fun WakeUpAction(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel) {
    //make smaller according to
    //val imeIsVisible = LocalWindowInsets.current.ime.isVisible
    BoxWithConstraints(
        modifier = modifier.padding(Dp(24f))
    ) {
        MainActionButton(this.maxHeight, viewModel)
    }
}

@Composable
fun MainActionButton(maxHeight: Dp, viewModel: HomeScreenViewModel) {

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

@Composable
fun MainActionFab(modifier: Modifier = Modifier, isMainActionBig: Boolean, viewModel: HomeScreenViewModel) {
    val iconSize = animateDpAsState(targetValue = if (isMainActionBig) 96.dp else 24.dp)

    Fab(modifier = modifier, iconSize = iconSize.value, viewModel)

}

@Composable
fun Fab(modifier: Modifier = Modifier, iconSize: Dp, viewModel: HomeScreenViewModel) {

    val requestMicrophonePermission =
        requestMicrophonePermission(LocalSnackbarHostState.current, MR.strings.microphonePermissionInfoRecord) {
            if (it) {
                viewModel.toggleSession()
            }
        }

    FloatingActionButton(
        onClick = {
            if (MicrophonePermission.granted.value) {
                viewModel.toggleSession()
            } else {
                requestMicrophonePermission.invoke()
            }
        },
        modifier = modifier,
        containerColor = if (viewModel.currentState.collectAsState().value == State.RecordingIntent) MaterialTheme.colorScheme.errorContainer else
            if (viewModel.currentState.collectAsState().value == State.AwaitingHotWord) MaterialTheme.colorScheme.primaryContainer else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
    ) {

        Icon(
            imageVector = if (MicrophonePermission.granted.collectAsState().value) Icons.Filled.Mic else Icons.Filled.MicOff,
            contentDescription = MR.strings.wakeUp,
            tint = if (viewModel.currentState.collectAsState().value == State.RecordingIntent) MaterialTheme.colorScheme.onErrorContainer else LocalContentColor.current,
            modifier = Modifier.size(iconSize)
        )
    }
}

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

@Composable
fun PlayRecording(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel
) {
    val isPlaying = viewModel.currentState.collectAsState().value == State.PlayingRecording

    ElevatedButton(
        onClick = { viewModel.togglePlayRecording() },
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