package org.rhasspy.mobile.android

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.viewModels.HomeScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

var isMainActionBig = mutableStateOf(true)
var mainActionVisible = mutableStateOf(true)

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimatedInsets::class
)
@Preview(showSystemUi = true)
@Composable
fun HomeScreen(viewModel : HomeScreenViewModel = viewModel()) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                WakeUpAction(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                )
                BottomActions(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
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
                        .fillMaxWidth()
                )
                BottomActions(modifier = Modifier.fillMaxWidth())
            }

        }
    }

}

@Composable
fun WakeUpAction(modifier: Modifier = Modifier) {
    //make smaller according to
    //val imeIsVisible = LocalWindowInsets.current.ime.isVisible
    BoxWithConstraints(
        modifier = modifier.padding(Dp(24f))
    ) {
        MainActionButton(this.maxHeight)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainActionButton(maxHeight: Dp) {

    isMainActionBig.value = maxHeight >= 96.dp + (24.dp * 2)
    mainActionVisible.value = maxHeight > 24.dp || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE


    AnimatedVisibility(
        enter = expandIn(expandFrom = Alignment.TopEnd),
        exit = shrinkOut(shrinkTowards = Alignment.Center),
        visible = mainActionVisible.value,
        modifier = Modifier.fillMaxSize()
    ) {
        MainActionFab(modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainActionFab(modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = { },
        modifier = modifier
    ) {
        val state = animateDpAsState(targetValue = if (isMainActionBig.value) 96.dp else 24.dp)

        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = MR.strings.wakeUp,
            modifier = Modifier
                .size(state.value)
        )
    }
}

@Composable
fun BottomActions(modifier: Modifier = Modifier) {
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

        TopRow()
        TextToRecognize(childModifier)
        TextToSpeak(childModifier)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopRow() {
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
            MainActionFab()
        }

        PlayRecording(modifier = Modifier.padding(horizontal = state.value))
    }
}

@Composable
fun PlayRecording(modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = { },
        modifier = modifier
    ) {
        Text(resource = MR.strings.playRecording)
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = MR.strings.playRecording
        )
    }
}

@Composable
fun TextToRecognize(
    modifier: Modifier = Modifier
) {
    var textToRecognize by remember { mutableStateOf("") }

    TextWithAction(
        modifier = modifier,
        label = MR.strings.textToRecognize,
        text = textToRecognize,
        onValueChange = { textToRecognize = it },
        onClick = {}) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = MR.strings.textToRecognize,
        )
    }
}

@Composable
fun TextToSpeak(
    modifier: Modifier = Modifier
) {
    var textToSpeak by remember { mutableStateOf("") }

    TextWithAction(
        modifier = modifier,
        label = MR.strings.textToSpeak,
        text = textToSpeak,
        onValueChange = { textToSpeak = it },
        onClick = {}) {
        Icon(
            imageVector = Icons.Filled.VolumeUp,
            contentDescription = MR.strings.textToSpeak,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TextWithAction(
    modifier: Modifier = Modifier,
    text: String,
    label: StringResource,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit, icon:
    @Composable () -> Unit
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            singleLine = true,
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .clearFocusOnKeyboardDismiss()
                .weight(1f)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            label = { Text(resource = label) }
        )
        ElevatedButton(
            modifier = Modifier.padding(horizontal = 24.dp),
            onClick = onClick
        ) {
            icon()
        }
    }
}