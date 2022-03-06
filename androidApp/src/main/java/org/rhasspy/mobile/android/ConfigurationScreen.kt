package org.rhasspy.mobile.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel
import java.math.RoundingMode

@Composable
fun ConfigurationScreen(viewModel: ConfigurationScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SiteId(viewModel)
        Divider()
        HttpSSL(viewModel)
        Divider()
        Mqtt()
        Divider()
        AudioRecording()
        Divider()
        WakeWord()
        Divider()
        SpeechToText()
        Divider()
        IntentRecognition()
        Divider()
        TextToSpeech()
        Divider()
        AudioPlaying()
        Divider()
        DialogueManagement()
        Divider()
        IntentHandling()
        Divider()
    }
}

@Composable
fun SiteId(viewModel: ConfigurationScreenViewModel) {

    val siteIdValue = viewModel.siteId.ld().observeAsState("").value

    TextFieldListItem(
        value = siteIdValue,
        onValueChange = { viewModel.siteId.value = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp)
    )
}

@Composable
fun HttpSSL(viewModel: ConfigurationScreenViewModel) {

    val isHttpSSLValue = viewModel.isHttpSSL.ld().observeAsState(false).value

    ExpandableListItem(
        text = MR.strings.httpSSL,
        secondaryText = isHttpSSLValue.toText()
    ) {

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isHttpSSLValue,
            onCheckedChange = { viewModel.isHttpSSL.value = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isHttpSSLValue
        ) {
            OutlineButtonListItem(
                text = MR.strings.chooseCertificate,
                onClick = { })
        }

    }
}

@Composable
fun Mqtt() {
    ExpandableListItem(
        text = MR.strings.mqtt,
        secondaryText = MR.strings.notConnected
    ) {
        var isMqttSSL by remember { mutableStateOf(false) }

        var mqttHost by remember { mutableStateOf("") }
        var mqttPort by remember { mutableStateOf("") }
        var mqttUserName by remember { mutableStateOf("") }
        var mqttPassword by remember { mutableStateOf("") }

        TextFieldListItem(
            label = MR.strings.host,
            value = mqttHost,
            onValueChange = { mqttHost = it },
        )

        TextFieldListItem(
            label = MR.strings.port,
            value = mqttPort,
            onValueChange = { mqttPort = it },
        )

        TextFieldListItem(
            value = mqttUserName,
            onValueChange = { mqttUserName = it },
            label = MR.strings.userName
        )

        var isShowPassword by rememberSaveable { mutableStateOf(false) }

        TextFieldListItem(
            value = mqttPassword,
            onValueChange = { mqttPassword = it },
            label = MR.strings.password,
            visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isShowPassword = !isShowPassword }) {
                    Icon(
                        if (isShowPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = MR.strings.visibility,
                    )
                }
            },
        )

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isMqttSSL,
            onCheckedChange = { isMqttSSL = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMqttSSL
        ) {
            OutlineButtonListItem(
                text = MR.strings.chooseCertificate,
                onClick = { })
        }

        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(onClick = { }) {
                Icon(Icons.Filled.Link, MR.strings.checkConnection)
                Spacer(modifier = Modifier.width(8.dp))
                Text(MR.strings.checkConnection)
            }
        }
    }
}

@Composable
fun AudioRecording() {
    var isUDPOutput by remember { mutableStateOf(false) }

    ExpandableListItem(
        text = MR.strings.audioRecording,
        secondaryText = if (isUDPOutput) MR.strings.udpAudioOutputOn else MR.strings.udpAudioOutputOff
    ) {

        SwitchListItem(
            text = MR.strings.udpAudioOutput,
            secondaryText = MR.strings.udpAudioOutputDetail,
            isChecked = isUDPOutput,
            onCheckedChange = { isUDPOutput = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {
            var udpOutputHost by remember { mutableStateOf("") }
            var udpOutputPort by remember { mutableStateOf("") }

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = udpOutputHost,
                    onValueChange = { udpOutputHost = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    value = udpOutputPort,
                    onValueChange = { udpOutputPort = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord() {
    var wakeWordValue by remember { mutableStateOf(WakeWordOption.Porcupine) }

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValue.text
    ) {

        DropDownEnumListItem(wakeWordValue, onSelect = { wakeWordValue = it }) { WakeWordOption.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = wakeWordValue == WakeWordOption.Porcupine
        ) {
            var accessTokenValue by remember { mutableStateOf("") }
            var keywordValue by remember { mutableStateOf(0f) }

            Column {
                TextFieldListItem(
                    value = accessTokenValue,
                    onValueChange = { accessTokenValue = it },
                    label = MR.strings.porcupineAccessKey
                )

                OutlineButtonListItem(
                    text = MR.strings.openPicoVoiceConsole,
                    onClick = { })

                //filled with correct values later
                var wakeWordValue2 by remember { mutableStateOf(WakeWordOption.Porcupine) }
                DropDownEnumListItem(wakeWordValue2, onSelect = { wakeWordValue2 = it }) { WakeWordOption.values() }

                SliderListItem(
                    text = MR.strings.sensitivity,
                    value = keywordValue,
                    onValueChange = { keywordValue = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat() })
            }
        }
    }
}

@Composable
fun SpeechToText() {
    var speechToTextValue by remember { mutableStateOf(SpeechToTextOptions.Disabled) }
    var speechToTextHttpEndpoint by remember { mutableStateOf("") }

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextValue.text
    ) {
        DropDownEnumListItem(speechToTextValue, onSelect = { speechToTextValue = it }) { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextValue == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = speechToTextHttpEndpoint,
                onValueChange = { speechToTextHttpEndpoint = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition() {
    var intentRecognitionValue by remember { mutableStateOf(IntentRecognitionOptions.Disabled) }
    var intentRecognitionEndpoint by remember { mutableStateOf("") }

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionValue.text
    ) {
        DropDownEnumListItem(intentRecognitionValue, onSelect = { intentRecognitionValue = it }) { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionValue == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = intentRecognitionEndpoint,
                onValueChange = { intentRecognitionEndpoint = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech() {
    var textToSpeechValue by remember { mutableStateOf(TextToSpeechOptions.Disabled) }
    var textToSpeechEndpoint by remember { mutableStateOf("") }

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechValue.text
    ) {
        DropDownEnumListItem(textToSpeechValue, onSelect = { textToSpeechValue = it }) { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechValue == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = textToSpeechEndpoint,
                onValueChange = { textToSpeechEndpoint = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying() {
    var audioPlayingValue by remember { mutableStateOf(AudioPlayingOptions.Disabled) }
    var audioPlayingEndpoint by remember { mutableStateOf("") }

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingValue.text
    ) {
        DropDownEnumListItem(audioPlayingValue, onSelect = { audioPlayingValue = it }) { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingValue == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = audioPlayingEndpoint,
                onValueChange = { audioPlayingEndpoint = it },
                label = MR.strings.audioOutputURL
            )

        }
    }
}

@Composable
fun DialogueManagement() {
    var dialogueManagementValue by remember { mutableStateOf(DialogueManagementOptions.Disabled) }

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementValue.text
    ) {
        DropDownEnumListItem(dialogueManagementValue, onSelect = { dialogueManagementValue = it }) { DialogueManagementOptions.values() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentHandling() {
    var intentHandlingValue by remember { mutableStateOf(IntentHandlingOptions.Disabled) }
    var intentHandlingEndpoint by remember { mutableStateOf("") }
    var intentHandlingHassUrl by remember { mutableStateOf("") }
    var intentHandlingHassAccessToken by remember { mutableStateOf("") }
    var isIntentHandlingHassEvent by remember { mutableStateOf(false) }

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingValue.text
    ) {
        DropDownEnumListItem(intentHandlingValue, onSelect = { intentHandlingValue = it }) { IntentHandlingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingValue == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = intentHandlingEndpoint,
                onValueChange = { intentHandlingEndpoint = it },
                label = MR.strings.remoteURL
            )
        }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingValue == IntentHandlingOptions.HomeAssistant
        ) {
            Column {

                TextFieldListItem(
                    value = intentHandlingHassUrl,
                    onValueChange = { intentHandlingHassUrl = it },
                    label = MR.strings.hassURL
                )

                TextFieldListItem(
                    value = intentHandlingHassAccessToken,
                    onValueChange = { intentHandlingHassAccessToken = it },
                    label = MR.strings.accessToken
                )

                RadioButtonListItem(
                    text = MR.strings.homeAssistantEvents,
                    isChecked = isIntentHandlingHassEvent,
                    onClick = {
                        isIntentHandlingHassEvent = true
                    })


                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    onClick = {
                        isIntentHandlingHassEvent = false
                    })
            }
        }
    }
}