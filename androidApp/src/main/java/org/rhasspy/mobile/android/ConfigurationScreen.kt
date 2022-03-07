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
        Mqtt(viewModel)
        Divider()
        AudioRecording(viewModel)
        Divider()
        WakeWord(viewModel)
        Divider()
        SpeechToText(viewModel)
        Divider()
        IntentRecognition(viewModel)
        Divider()
        TextToSpeech(viewModel)
        Divider()
        AudioPlaying(viewModel)
        Divider()
        DialogueManagement(viewModel)
        Divider()
        IntentHandling(viewModel)
        Divider()
    }
}

@Composable
fun SiteId(viewModel: ConfigurationScreenViewModel) {

    TextFieldListItem(
        value = viewModel.siteId.observe(),
        onValueChange = { viewModel.siteId.value = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp)
    )
}

@Composable
fun HttpSSL(viewModel: ConfigurationScreenViewModel) {

    val isHttpSSLValue = viewModel.isHttpSSL.observe()

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
fun Mqtt(viewModel: ConfigurationScreenViewModel) {
    ExpandableListItem(
        text = MR.strings.mqtt,
        secondaryText = MR.strings.notConnected
    ) {

        TextFieldListItem(
            label = MR.strings.host,
            value = viewModel.mqttHost.observe(),
            onValueChange = { viewModel.mqttHost.value = it },
        )

        TextFieldListItem(
            label = MR.strings.port,
            value = viewModel.mqttPort.observe(),
            onValueChange = { viewModel.mqttPort.value = it },
        )

        TextFieldListItem(
            value = viewModel.mqttUserName.observe(),
            onValueChange = { viewModel.mqttUserName.value = it },
            label = MR.strings.userName
        )

        var isShowPassword by rememberSaveable { mutableStateOf(false) }

        TextFieldListItem(
            value = viewModel.mqttPassword.observe(),
            onValueChange = { viewModel.mqttPassword.value = it },
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

        val isMqttSSL = viewModel.isMqttSSL.observe()

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isMqttSSL,
            onCheckedChange = { viewModel.isMqttSSL.value = it })

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
fun AudioRecording(viewModel: ConfigurationScreenViewModel) {

    val isUDPOutput = viewModel.isUDPOutput.observe()

    ExpandableListItem(
        text = MR.strings.audioRecording,
        secondaryText =
        if (isUDPOutput)
            MR.strings.udpAudioOutputOn
        else
            MR.strings.udpAudioOutputOff
    ) {

        SwitchListItem(
            text = MR.strings.udpAudioOutput,
            secondaryText = MR.strings.udpAudioOutputDetail,
            isChecked = isUDPOutput,
            onCheckedChange = { viewModel.isUDPOutput.value = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = viewModel.udpOutputHost.observe(),
                    onValueChange = { viewModel.udpOutputHost.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    value = viewModel.udpOutputPort.observe(),
                    onValueChange = { viewModel.udpOutputPort.value = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord(viewModel: ConfigurationScreenViewModel) {

    val wakeWordValueOption = viewModel.wakeWordValueOption.observe()

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text
    ) {

        DropDownEnumListItem(
            selected = wakeWordValueOption,
            onSelect = { viewModel.wakeWordValueOption.value = it })
        { WakeWordOption.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.wakeWordValueOption.value == WakeWordOption.Porcupine
        ) {

            Column {
                TextFieldListItem(
                    value = viewModel.wakeWordAccessToken.observe(),
                    onValueChange = { viewModel.wakeWordAccessToken.value = it },
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
                    value = viewModel.wakeWordKeyword.observe(),
                    onValueChange = { viewModel.wakeWordKeyword.value = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat() })
            }
        }
    }
}

@Composable
fun SpeechToText(viewModel: ConfigurationScreenViewModel) {

    val speechToTextOption = viewModel.speechToTextOption.observe()

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextOption.text
    ) {
        DropDownEnumListItem(
            selected = speechToTextOption,
            onSelect = { viewModel.speechToTextOption.value = it })
        { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.speechToTextHttpEndpoint.observe(),
                onValueChange = { viewModel.speechToTextHttpEndpoint.value = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition(viewModel: ConfigurationScreenViewModel) {

    val intentRecognitionOption = viewModel.intentRecognitionOption.observe()

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionOption.text
    ) {
        DropDownEnumListItem(
            selected = intentRecognitionOption,
            onSelect = { viewModel.intentRecognitionOption.value = it })
        { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.intentRecognitionEndpoint.observe(),
                onValueChange = { viewModel.intentRecognitionEndpoint.value = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech(viewModel: ConfigurationScreenViewModel) {

    val textToSpeechOption = viewModel.textToSpeechOption.observe()

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechOption.text
    ) {
        DropDownEnumListItem(
            selected = textToSpeechOption,
            onSelect = { viewModel.textToSpeechOption.value = it })
        { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.textToSpeechEndpoint.observe(),
                onValueChange = { viewModel.textToSpeechEndpoint.value = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying(viewModel: ConfigurationScreenViewModel) {

    val audioPlayingOption = viewModel.audioPlayingOption.observe()

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingOption.text
    ) {
        DropDownEnumListItem(
            selected = audioPlayingOption,
            onSelect = { viewModel.audioPlayingOption.value = it })
        { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.audioPlayingEndpoint.observe(),
                onValueChange = { viewModel.audioPlayingEndpoint.value = it },
                label = MR.strings.audioOutputURL
            )
        }
    }
}

@Composable
fun DialogueManagement(viewModel: ConfigurationScreenViewModel) {

    val dialogueManagementOption = viewModel.dialogueManagementOption.observe()

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementOption.text
    ) {
        DropDownEnumListItem(
            selected = dialogueManagementOption,
            onSelect = { viewModel.dialogueManagementOption.value = it })
        { DialogueManagementOptions.values() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentHandling(viewModel: ConfigurationScreenViewModel) {

    val intentHandlingOption = viewModel.intentHandlingOption.observe()

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingOption.text
    ) {
        DropDownEnumListItem(
            selected = intentHandlingOption,
            onSelect = { viewModel.intentHandlingOption.value = it })
        { IntentHandlingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.intentHandlingEndpoint.observe(),
                onValueChange = { viewModel.intentHandlingEndpoint.value = it },
                label = MR.strings.remoteURL
            )
        }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.HomeAssistant
        ) {
            Column {

                TextFieldListItem(
                    value = viewModel.intentHandlingHassUrl.observe(),
                    onValueChange = { viewModel.intentHandlingHassUrl.value = it },
                    label = MR.strings.hassURL
                )

                TextFieldListItem(
                    value = viewModel.intentHandlingHassAccessToken.observe(),
                    onValueChange = { viewModel.intentHandlingHassAccessToken.value = it },
                    label = MR.strings.accessToken
                )

                val isIntentHandlingHassEvent = viewModel.isIntentHandlingHassEvent.observe()

                RadioButtonListItem(
                    text = MR.strings.homeAssistantEvents,
                    isChecked = isIntentHandlingHassEvent,
                    onClick = {
                        viewModel.isIntentHandlingHassEvent.value = true
                    })


                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    onClick = {
                        viewModel.isIntentHandlingHassEvent.value = false
                    })
            }
        }
    }
}