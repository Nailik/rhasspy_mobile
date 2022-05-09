package org.rhasspy.mobile.android.bottomBarScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel
import java.math.RoundingMode

@Composable
fun ConfigurationScreen(snackbarHostState: SnackbarHostState, viewModel: ConfigurationScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SiteId()
        CustomDivider()
        HttpServer()
        CustomDivider()
        Mqtt(viewModel)
        CustomDivider()
        AudioRecording()
        CustomDivider()
        WakeWord(viewModel, snackbarHostState)
        CustomDivider()
        SpeechToText()
        CustomDivider()
        IntentRecognition()
        CustomDivider()
        TextToSpeech()
        CustomDivider()
        AudioPlaying()
        CustomDivider()
        DialogueManagement()
        CustomDivider()
        IntentHandling()
        CustomDivider()
    }
}


@Composable
fun SiteId() {

    TextFieldListItem(
        value = ConfigurationSettings.siteId.observeCurrent(),
        onValueChange = { ConfigurationSettings.siteId.unsavedData = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp),
        enabled = !ServiceInterface.isRestarting.observe()
    )
}

@Composable
fun HttpServer() {

    val isHttpServerValue = ConfigurationSettings.isHttpServerEnabled.observeCurrent()

    ExpandableListItem(
        text = MR.strings.webserver,
        secondaryText = isHttpServerValue.toText()
    ) {

        SwitchListItem(
            text = MR.strings.enableHTTPApi,
            isChecked = isHttpServerValue,
            enabled = !ServiceInterface.isRestarting.observe(),
            onCheckedChange = { ConfigurationSettings.isHttpServerEnabled.unsavedData = it })

        TextFieldListItem(
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = ConfigurationSettings.httpServerPort.observeCurrent().toString(),
            enabled = !ServiceInterface.isRestarting.observe(),
            onValueChange = { ConfigurationSettings.httpServerPort.unsavedData = it },
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isHttpServerValue
        ) {

            Column {

                val isHttpServerSSLValue = ConfigurationSettings.isHttpServerSSL.observeCurrent()

                SwitchListItem(
                    text = MR.strings.enableSSL,
                    isChecked = isHttpServerSSLValue,
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onCheckedChange = { ConfigurationSettings.isHttpServerSSL.unsavedData = it })

                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = isHttpServerSSLValue
                ) {
                    OutlineButtonListItem(
                        text = MR.strings.chooseCertificate,
                        enabled = !ServiceInterface.isRestarting.observe(),
                        onClick = { })
                }
            }
        }
    }
}

@Composable
fun Mqtt(viewModel: ConfigurationScreenViewModel) {
    ExpandableListItem(
        text = MR.strings.mqtt,
        secondaryText = if (viewModel.isMQTTConnected.observe()) MR.strings.connected else MR.strings.notConnected
    ) {

        val isMqttEnabled = ConfigurationSettings.isMQTTEnabled.observeCurrent()

        SwitchListItem(
            text = MR.strings.externalMQTT,
            isChecked = isMqttEnabled,
            enabled = !ServiceInterface.isRestarting.observe(),
            onCheckedChange = { ConfigurationSettings.isMQTTEnabled.unsavedData = it })


        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMqttEnabled
        ) {

            Column {

                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationSettings.mqttHost.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.mqttHost.unsavedData = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.mqttPort.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.mqttPort.unsavedData = it },
                )

                TextFieldListItem(
                    value = ConfigurationSettings.mqttUserName.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.mqttUserName.unsavedData = it },
                    label = MR.strings.userName
                )

                var isShowPassword by rememberSaveable { mutableStateOf(false) }

                TextFieldListItem(
                    value = ConfigurationSettings.mqttPassword.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.mqttPassword.unsavedData = it },
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

                val isMqttSSL = ConfigurationSettings.isMqttSSL.observeCurrent()

                SwitchListItem(
                    text = MR.strings.enableSSL,
                    isChecked = isMqttSSL,
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onCheckedChange = { ConfigurationSettings.isMqttSSL.unsavedData = it })

                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = isMqttSSL
                ) {
                    OutlineButtonListItem(
                        text = MR.strings.chooseCertificate,
                        enabled = !ServiceInterface.isRestarting.observe(),
                        onClick = { })
                }
            }
        }
    }
}

@Composable
fun AudioRecording() {

    val isUDPOutput = ConfigurationSettings.isUDPOutput.observeCurrent()

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
            enabled = !ServiceInterface.isRestarting.observe(),
            onCheckedChange = { ConfigurationSettings.isUDPOutput.unsavedData = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationSettings.udpOutputHost.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.udpOutputHost.unsavedData = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.udpOutputPort.observeCurrent().toString(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.udpOutputPort.unsavedData = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord(viewModel: ConfigurationScreenViewModel, snackbarHostState: SnackbarHostState) {

    val wakeWordValueOption = ConfigurationSettings.wakeWordOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text
    ) {

        val requestMicrophonePermission = requestMicrophonePermission(snackbarHostState, MR.strings.microphonePermissionInfoWakeWord) {
            if (it) {
                ConfigurationSettings.wakeWordOption.unsavedData = WakeWordOption.Porcupine
            }
        }

        DropDownEnumListItem(
            selected = wakeWordValueOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = {
                if (it == WakeWordOption.Porcupine && !MicrophonePermission.granted.value) {
                    requestMicrophonePermission.invoke()
                } else {
                    ConfigurationSettings.wakeWordOption.unsavedData = it
                }
            })
        { WakeWordOption.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = ConfigurationSettings.wakeWordOption.observeCurrent() == WakeWordOption.Porcupine
        ) {

            Column {


                var isShowAccessToken by rememberSaveable { mutableStateOf(false) }

                TextFieldListItem(
                    value = ConfigurationSettings.wakeWordPorcupineAccessToken.observeCurrent(),
                    onValueChange = { ConfigurationSettings.wakeWordPorcupineAccessToken.unsavedData = it },
                    label = MR.strings.porcupineAccessKey,
                    visualTransformation = if (isShowAccessToken) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isShowAccessToken = !isShowAccessToken }) {
                            Icon(
                                if (isShowAccessToken) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = MR.strings.visibility,
                            )
                        }
                    },
                )
                val context = LocalContext.current

                OutlineButtonListItem(
                    text = MR.strings.openPicoVoiceConsole,
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://console.picovoice.ai")))
                    })

                //filled with correct values later
                DropDownListWithFileOpen(
                    overlineText = { Text(MR.strings.wakeWord) },
                    selected = ConfigurationSettings.wakeWordPorcupineKeywordOption.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    values = ConfigurationSettings.wakeWordPorcupineKeywordOptions.observeCurrent().toTypedArray(),
                    onAdd = {
                        viewModel.selectPorcupineWakeWordFile()
                    }) {
                    ConfigurationSettings.wakeWordPorcupineKeywordOption.unsavedData = it
                }

                DropDownEnumListItem(
                    selected = ConfigurationSettings.wakeWordPorcupineLanguage.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onSelect = { ConfigurationSettings.wakeWordPorcupineLanguage.unsavedData = it })
                { PorcupineLanguageOptions.values() }

                SliderListItem(
                    text = MR.strings.sensitivity,
                    value = ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = {
                        ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.unsavedData =
                            it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
                    })
            }
        }
    }
}

@Composable
fun SpeechToText() {

    val speechToTextOption = ConfigurationSettings.speechToTextOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextOption.text
    ) {
        DropDownEnumListItem(
            selected = speechToTextOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = { ConfigurationSettings.speechToTextOption.unsavedData = it })
        { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.speechToTextHttpEndpoint.observeCurrent(),
                enabled = !ServiceInterface.isRestarting.observe(),
                onValueChange = { ConfigurationSettings.speechToTextHttpEndpoint.unsavedData = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition() {

    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionOption.text
    ) {
        DropDownEnumListItem(
            selected = intentRecognitionOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = { ConfigurationSettings.intentRecognitionOption.unsavedData = it })
        { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentRecognitionEndpoint.observeCurrent(),
                enabled = !ServiceInterface.isRestarting.observe(),
                onValueChange = { ConfigurationSettings.intentRecognitionEndpoint.unsavedData = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech() {

    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechOption.text
    ) {
        DropDownEnumListItem(
            selected = textToSpeechOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = { ConfigurationSettings.textToSpeechOption.unsavedData = it })
        { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.textToSpeechEndpoint.observeCurrent(),
                enabled = !ServiceInterface.isRestarting.observe(),
                onValueChange = { ConfigurationSettings.textToSpeechEndpoint.unsavedData = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying() {

    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingOption.text
    ) {
        DropDownEnumListItem(
            selected = audioPlayingOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = { ConfigurationSettings.audioPlayingOption.unsavedData = it })
        { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.audioPlayingEndpoint.observeCurrent(),
                enabled = !ServiceInterface.isRestarting.observe(),
                onValueChange = { ConfigurationSettings.audioPlayingEndpoint.unsavedData = it },
                label = MR.strings.audioOutputURL
            )
        }
    }
}

@Composable
fun DialogueManagement() {

    val dialogueManagementOption = ConfigurationSettings.dialogueManagementOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementOption.text
    ) {
        DropDownEnumListItem(
            selected = dialogueManagementOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = { ConfigurationSettings.dialogueManagementOption.unsavedData = it })
        { DialogueManagementOptions.values() }
    }
}

@Composable
fun IntentHandling() {

    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingOption.text
    ) {
        DropDownEnumListItem(
            selected = intentHandlingOption,
            enabled = !ServiceInterface.isRestarting.observe(),
            onSelect = {
                ConfigurationSettings.intentHandlingOption.unsavedData = it
            })
        { IntentHandlingOptions.values() }


        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentHandlingEndpoint.observeCurrent(),
                enabled = !ServiceInterface.isRestarting.observe(),
                onValueChange = { ConfigurationSettings.intentHandlingEndpoint.unsavedData = it },
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
                    value = ConfigurationSettings.intentHandlingHassUrl.observeCurrent(),
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onValueChange = { ConfigurationSettings.intentHandlingHassUrl.unsavedData = it },
                    label = MR.strings.hassURL
                )

                var isShowAccessToken by rememberSaveable { mutableStateOf(false) }

                TextFieldListItem(
                    value = ConfigurationSettings.intentHandlingHassAccessToken.observeCurrent(),
                    onValueChange = { ConfigurationSettings.intentHandlingHassAccessToken.unsavedData = it },
                    label = MR.strings.accessToken,
                    visualTransformation = if (isShowAccessToken) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isShowAccessToken = !isShowAccessToken }) {
                            Icon(
                                if (isShowAccessToken) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = MR.strings.visibility,
                            )
                        }
                    },
                )

                val isIntentHandlingHassEvent = ConfigurationSettings.isIntentHandlingHassEvent.observeCurrent()

                RadioButtonListItem(
                    text = MR.strings.homeAssistantEvents,
                    isChecked = isIntentHandlingHassEvent,
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsavedData = true
                    })

                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    enabled = !ServiceInterface.isRestarting.observe(),
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsavedData = false
                    })
            }
        }
    }
}