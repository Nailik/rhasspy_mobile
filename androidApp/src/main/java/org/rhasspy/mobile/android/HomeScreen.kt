package org.rhasspy.mobile.android

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Preview
@Composable
fun HomeScreen() {

    Column {
        WakeUpAction()
        BottomActions()
    }
}

@Composable
fun ColumnScope.WakeUpAction() {
    FloatingActionButton(
        onClick = { },
        modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth()
            .padding(Dp(24f))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = MR.strings.wakeUp,
                modifier = Modifier.size(Dp(96f))
            )
            Text(resource = MR.strings.wakeUp)
        }
    }
}

@Composable
fun BottomActions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dp(24f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayRecording()
        TextToRecognize()
        TextToSpeak()
    }
}

@Composable
fun PlayRecording() {
    ElevatedButton(onClick = { }) {
        Text(resource = MR.strings.playRecording)
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = MR.strings.playRecording
        )
    }
}

@Composable
fun TextToRecognize() {
    var textToRecognize by remember { mutableStateOf("") }

    TextWithAction(
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
fun TextToSpeak() {
    var textToSpeak by remember { mutableStateOf("") }

    TextWithAction(
        label = MR.strings.textToSpeak,
        text = textToSpeak,
        onValueChange = { textToSpeak = it },
        onClick = {}) {
        Icon(
            imageVector = Icons.Filled.Speaker,
            contentDescription = MR.strings.textToSpeak,
        )
    }
}

@Composable
fun TextWithAction(text: String, label: StringResource, onValueChange: (String) -> Unit, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .clearFocusOnKeyboardDismiss(),
            label = { Text(resource = label) }
        )
        ElevatedButton(
            onClick = onClick
        ) {
            icon()
        }
    }
}