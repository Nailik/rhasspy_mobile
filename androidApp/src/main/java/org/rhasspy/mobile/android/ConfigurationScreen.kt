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
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.viewModels.ConfigurationData
import java.math.RoundingMode

@Composable
fun ConfigurationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SiteId()
        Divider()
        HttpSSL()
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
fun SiteId() {

    TextFieldListItem(
        value = ConfigurationData.siteId.observe(),
        onValueChange = { ConfigurationData.siteId.value = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp)
    )
}

@Composable
fun HttpSSL() {

    val isHttpSSLValue = ConfigurationData.isHttpSSL.observe()

    ExpandableListItem(
        text = MR.strings.httpSSL,
        secondaryText = isHttpSSLValue.toText()
    ) {

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isHttpSSLValue,
            onCheckedChange = { ConfigurationData.isHttpSSL.value = it })

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

        TextFieldListItem(
            label = MR.strings.host,
            value = ConfigurationData.mqttHost.observe(),
            onValueChange = { ConfigurationData.mqttHost.value = it },
        )

        TextFieldListItem(
            label = MR.strings.port,
            value = ConfigurationData.mqttPort.observe(),
            onValueChange = { ConfigurationData.mqttPort.value = it },
        )

        TextFieldListItem(
            value = ConfigurationData.mqttUserName.observe(),
            onValueChange = { ConfigurationData.mqttUserName.value = it },
            label = MR.strings.userName
        )

        var isShowPassword by rememberSaveable { mutableStateOf(false) }

        TextFieldListItem(
            value = ConfigurationData.mqttPassword.observe(),
            onValueChange = { ConfigurationData.mqttPassword.value = it },
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

        val isMqttSSL = ConfigurationData.isMqttSSL.observe()

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isMqttSSL,
            onCheckedChange = { ConfigurationData.isMqttSSL.value = it })

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

    val isUDPOutput = ConfigurationData.isUDPOutput.observe()

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
            onCheckedChange = { ConfigurationData.isUDPOutput.value = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationData.udpOutputHost.observe(),
                    onValueChange = { ConfigurationData.udpOutputHost.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    value = ConfigurationData.udpOutputPort.observe(),
                    onValueChange = { ConfigurationData.udpOutputPort.value = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord() {

    val wakeWordValueOption = ConfigurationData.wakeWordValueOption.observe()

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text
    ) {

        DropDownEnumListItem(
            selected = wakeWordValueOption,
            onSelect = { ConfigurationData.wakeWordValueOption.value = it })
        { WakeWordOption.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = ConfigurationData.wakeWordValueOption.value == WakeWordOption.Porcupine
        ) {

            Column {
                TextFieldListItem(
                    value = ConfigurationData.wakeWordAccessToken.observe(),
                    onValueChange = { ConfigurationData.wakeWordAccessToken.value = it },
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
                    value = ConfigurationData.wakeWordKeyword.observe(),
                    onValueChange = { ConfigurationData.wakeWordKeyword.value = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat() })
            }
        }
    }
}

@Composable
fun SpeechToText() {

    val speechToTextOption = ConfigurationData.speechToTextOption.observe()

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextOption.text
    ) {
        DropDownEnumListItem(
            selected = speechToTextOption,
            onSelect = { ConfigurationData.speechToTextOption.value = it })
        { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationData.speechToTextHttpEndpoint.observe(),
                onValueChange = { ConfigurationData.speechToTextHttpEndpoint.value = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition() {

    val intentRecognitionOption = ConfigurationData.intentRecognitionOption.observe()

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionOption.text
    ) {
        DropDownEnumListItem(
            selected = intentRecognitionOption,
            onSelect = { ConfigurationData.intentRecognitionOption.value = it })
        { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationData.intentRecognitionEndpoint.observe(),
                onValueChange = { ConfigurationData.intentRecognitionEndpoint.value = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech() {

    val textToSpeechOption = ConfigurationData.textToSpeechOption.observe()

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechOption.text
    ) {
        DropDownEnumListItem(
            selected = textToSpeechOption,
            onSelect = { ConfigurationData.textToSpeechOption.value = it })
        { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationData.textToSpeechEndpoint.observe(),
                onValueChange = { ConfigurationData.textToSpeechEndpoint.value = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying() {

    val audioPlayingOption = ConfigurationData.audioPlayingOption.observe()

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingOption.text
    ) {
        DropDownEnumListItem(
            selected = audioPlayingOption,
            onSelect = { ConfigurationData.audioPlayingOption.value = it })
        { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationData.audioPlayingEndpoint.observe(),
                onValueChange = { ConfigurationData.audioPlayingEndpoint.value = it },
                label = MR.strings.audioOutputURL
            )
        }
    }
}

@Composable
fun DialogueManagement() {

    val dialogueManagementOption = ConfigurationData.dialogueManagementOption.observe()

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementOption.text
    ) {
        DropDownEnumListItem(
            selected = dialogueManagementOption,
            onSelect = { ConfigurationData.dialogueManagementOption.value = it })
        { DialogueManagementOptions.values() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentHandling() {

    val intentHandlingOption = ConfigurationData.intentHandlingOption.observe()

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingOption.text
    ) {
        DropDownEnumListItem(
            selected = intentHandlingOption,
            onSelect = { ConfigurationData.intentHandlingOption.value = it })
        { IntentHandlingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationData.intentHandlingEndpoint.observe(),
                onValueChange = { ConfigurationData.intentHandlingEndpoint.value = it },
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
                    value = ConfigurationData.intentHandlingHassUrl.observe(),
                    onValueChange = { ConfigurationData.intentHandlingHassUrl.value = it },
                    label = MR.strings.hassURL
                )

                TextFieldListItem(
                    value = ConfigurationData.intentHandlingHassAccessToken.observe(),
                    onValueChange = { ConfigurationData.intentHandlingHassAccessToken.value = it },
                    label = MR.strings.accessToken
                )

                val isIntentHandlingHassEvent = ConfigurationData.isIntentHandlingHassEvent.observe()

                RadioButtonListItem(
                    text = MR.strings.homeAssistantEvents,
                    isChecked = isIntentHandlingHassEvent,
                    onClick = {
                        ConfigurationData.isIntentHandlingHassEvent.value = true
                    })


                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    onClick = {
                        ConfigurationData.isIntentHandlingHassEvent.value = false
                    })
            }
        }
    }
}