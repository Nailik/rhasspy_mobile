package org.rhasspy.mobile.android.bottomBarScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
        val isEnabled = viewModel.isChangeEnabled.observe()
        SiteId(isEnabled)
        CustomDivider()
        Webserver(isEnabled)
        CustomDivider()
        RemoteHermesHTTP(isEnabled)
        CustomDivider()
        Mqtt(viewModel, isEnabled && !viewModel.isTestingMqttConnection.observe())
        CustomDivider()
        AudioRecording(isEnabled)
        CustomDivider()
        WakeWord(viewModel, isEnabled, snackbarHostState)
        CustomDivider()
        SpeechToText(isEnabled)
        CustomDivider()
        IntentRecognition(isEnabled)
        CustomDivider()
        TextToSpeech(isEnabled)
        CustomDivider()
        AudioPlaying(isEnabled)
        CustomDivider()
        DialogueManagement(isEnabled)
        CustomDivider()
        IntentHandling(isEnabled)
        CustomDivider()
    }
}


@Composable
fun SiteId(enabled: Boolean) {

    TextFieldListItem(
        value = ConfigurationSettings.siteId.observeCurrent(),
        onValueChange = { ConfigurationSettings.siteId.unsaved.value = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp),
        enabled = enabled
    )
}

@Composable
fun Webserver(enabled: Boolean) {

    val isHttpServerValue = ConfigurationSettings.isHttpServerEnabled.observeCurrent()

    ExpandableListItem(
        text = MR.strings.webserver,
        secondaryText = isHttpServerValue.toText()
    ) {

        SwitchListItem(
            text = MR.strings.enableHTTPApi,
            isChecked = isHttpServerValue,
            enabled = enabled,
            onCheckedChange = { ConfigurationSettings.isHttpServerEnabled.unsaved.value = it })

        TextFieldListItem(
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = ConfigurationSettings.httpServerPort.observeCurrent().toString(),
            enabled = enabled,
            onValueChange = { ConfigurationSettings.httpServerPort.unsaved.value = it },
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
                    enabled = enabled,
                    onCheckedChange = { ConfigurationSettings.isHttpServerSSL.unsaved.value = it })

                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = isHttpServerSSLValue
                ) {
                    OutlineButtonListItem(
                        text = MR.strings.chooseCertificate,
                        enabled = enabled,
                        onClick = { })
                }
            }
        }
    }
}


@Composable
fun RemoteHermesHTTP(enabled: Boolean) {

    val isSSLVerificationEnabled = ConfigurationSettings.isSSLVerificationEnabled.observeCurrent()

    ExpandableListItemString(
        text = MR.strings.remoteHermesHTTP,
        secondaryText = "${translate(MR.strings.sslValidation)} ${translate(isSSLVerificationEnabled.toText())}"
    ) {

        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            enabled = enabled,
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = !isSSLVerificationEnabled,
            onCheckedChange = { ConfigurationSettings.isSSLVerificationEnabled.unsaved.value = !it })

    }
}


@Composable
fun Mqtt(viewModel: ConfigurationScreenViewModel, enabled: Boolean) {
    ExpandableListItem(
        text = MR.strings.mqtt,
        secondaryText = if (viewModel.isMQTTConnected.observe()) MR.strings.connected else MR.strings.notConnected
    ) {

        val isMqttEnabled = ConfigurationSettings.isMQTTEnabled.observeCurrent()

        SwitchListItem(
            text = MR.strings.externalMQTT,
            isChecked = isMqttEnabled,
            enabled = enabled,
            onCheckedChange = { ConfigurationSettings.isMQTTEnabled.unsaved.value = it })


        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMqttEnabled
        ) {

            Column {

                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationSettings.mqttHost.observeCurrent(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttHost.unsaved.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.mqttPort.observeCurrent(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttPort.unsaved.value = it },
                )

                TextFieldListItem(
                    value = ConfigurationSettings.mqttUserName.observeCurrent(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttUserName.unsaved.value = it },
                    label = MR.strings.userName
                )

                var isShowPassword by rememberSaveable { mutableStateOf(false) }

                TextFieldListItem(
                    value = ConfigurationSettings.mqttPassword.observeCurrent(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttPassword.unsaved.value = it },
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

                OutlineButtonListItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (viewModel.isTestingMqttConnection.observe()) {

                                val infiniteTransition = rememberInfiniteTransition()
                                val angle by infiniteTransition.animateFloat(
                                    initialValue = 0F,
                                    targetValue = 360F,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, easing = LinearEasing)
                                    )
                                )

                                Icon(
                                    modifier = Modifier.rotate(angle),
                                    imageVector = Icons.Filled.Autorenew,
                                    contentDescription = MR.strings.reset
                                )
                            }

                            viewModel.testingMqttErrorUiData.observe()?.also {
                                androidx.compose.material3.Text(text = "${translate(MR.strings.error)}: ${it.statusCode.name}")
                            } ?: run {
                                Text(MR.strings.testConnection)
                            }
                        }
                    },
                    enabled = ConfigurationSettings.mqttHost.unsaved.observe().isNotEmpty() &&
                            ConfigurationSettings.mqttPassword.unsaved.observe().isNotEmpty(),
                    onClick = {
                        viewModel.testMqttConnection()
                    })

                val isMqttSSL = ConfigurationSettings.isMqttSSL.observeCurrent()

                SwitchListItem(
                    text = MR.strings.enableSSL,
                    isChecked = isMqttSSL,
                    enabled = enabled,
                    onCheckedChange = { ConfigurationSettings.isMqttSSL.unsaved.value = it })

                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = isMqttSSL
                ) {
                    OutlineButtonListItem(
                        text = MR.strings.chooseCertificate,
                        enabled = enabled,
                        onClick = { })
                }


                TextFieldListItem(
                    label = MR.strings.connectionTimeout,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.mqttConnectionTimeout.observeCurrent().toString(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttConnectionTimeout.unsaved.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.keepAliveInterval,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.mqttKeepAliveInterval.observeCurrent().toString(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttKeepAliveInterval.unsaved.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.retryInterval,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.mqttRetryInterval.observeCurrent().toString(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.mqttRetryInterval.unsaved.value = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun AudioRecording(enabled: Boolean) {

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
            enabled = enabled,
            onCheckedChange = { ConfigurationSettings.isUDPOutput.unsaved.value = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationSettings.udpOutputHost.observeCurrent(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.udpOutputHost.unsaved.value = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = ConfigurationSettings.udpOutputPort.observeCurrent().toString(),
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.udpOutputPort.unsaved.value = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord(viewModel: ConfigurationScreenViewModel, enabled: Boolean, snackbarHostState: SnackbarHostState) {

    val wakeWordValueOption = ConfigurationSettings.wakeWordOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text
    ) {

        val requestMicrophonePermission = requestMicrophonePermission(snackbarHostState, MR.strings.microphonePermissionInfoWakeWord) {
            if (it) {
                ConfigurationSettings.wakeWordOption.unsaved.value = WakeWordOption.Porcupine
            }
        }

        DropDownEnumListItem(
            selected = wakeWordValueOption,
            enabled = enabled,
            onSelect = {
                if (it == WakeWordOption.Porcupine && !MicrophonePermission.granted.value) {
                    requestMicrophonePermission.invoke()
                } else {
                    ConfigurationSettings.wakeWordOption.unsaved.value = it
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
                    onValueChange = { ConfigurationSettings.wakeWordPorcupineAccessToken.unsaved.value = it },
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
                    enabled = enabled,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://console.picovoice.ai")))
                    })

                //filled with correct values later
                DropDownListWithFileOpen(
                    overlineText = { Text(MR.strings.wakeWord) },
                    selected = ConfigurationSettings.wakeWordPorcupineKeywordOption.observeCurrent(),
                    enabled = enabled,
                    values = ConfigurationSettings.wakeWordPorcupineKeywordOptions.observeCurrent().toTypedArray(),
                    onAdd = {
                        viewModel.selectPorcupineWakeWordFile()
                    }) {
                    ConfigurationSettings.wakeWordPorcupineKeywordOption.unsaved.value = it
                }

                DropDownEnumListItem(
                    selected = ConfigurationSettings.wakeWordPorcupineLanguage.observeCurrent(),
                    enabled = enabled,
                    onSelect = { ConfigurationSettings.wakeWordPorcupineLanguage.unsaved.value = it })
                { PorcupineLanguageOptions.values() }

                SliderListItem(
                    text = MR.strings.sensitivity,
                    value = ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.observeCurrent(),
                    enabled = enabled,
                    onValueChange = {
                        ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.unsaved.value =
                            it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
                    })
            }
        }
    }
}

@Composable
fun SpeechToText(enabled: Boolean) {

    val speechToTextOption = ConfigurationSettings.speechToTextOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextOption.text
    ) {
        DropDownEnumListItem(
            selected = speechToTextOption,
            enabled = enabled,
            onSelect = { ConfigurationSettings.speechToTextOption.unsaved.value = it })
        { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.speechToTextHttpEndpoint.observeCurrent(),
                enabled = enabled,
                onValueChange = { ConfigurationSettings.speechToTextHttpEndpoint.unsaved.value = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition(enabled: Boolean) {

    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionOption.text
    ) {
        DropDownEnumListItem(
            selected = intentRecognitionOption,
            enabled = enabled,
            onSelect = { ConfigurationSettings.intentRecognitionOption.unsaved.value = it })
        { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentRecognitionEndpoint.observeCurrent(),
                enabled = enabled,
                onValueChange = { ConfigurationSettings.intentRecognitionEndpoint.unsaved.value = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech(enabled: Boolean) {

    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechOption.text
    ) {
        DropDownEnumListItem(
            selected = textToSpeechOption,
            enabled = enabled,
            onSelect = { ConfigurationSettings.textToSpeechOption.unsaved.value = it })
        { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.textToSpeechEndpoint.observeCurrent(),
                enabled = enabled,
                onValueChange = { ConfigurationSettings.textToSpeechEndpoint.unsaved.value = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying(enabled: Boolean) {

    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingOption.text
    ) {
        DropDownEnumListItem(
            selected = audioPlayingOption,
            enabled = enabled,
            onSelect = { ConfigurationSettings.audioPlayingOption.unsaved.value = it })
        { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.audioPlayingEndpoint.observeCurrent(),
                enabled = enabled,
                onValueChange = { ConfigurationSettings.audioPlayingEndpoint.unsaved.value = it },
                label = MR.strings.audioOutputURL
            )
        }
    }
}

@Composable
fun DialogueManagement(enabled: Boolean) {

    val dialogueManagementOption = ConfigurationSettings.dialogueManagementOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementOption.text
    ) {
        DropDownEnumListItem(
            selected = dialogueManagementOption,
            enabled = enabled,
            onSelect = { ConfigurationSettings.dialogueManagementOption.unsaved.value = it })
        { DialogueManagementOptions.values() }
    }
}

@Composable
fun IntentHandling(enabled: Boolean) {

    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingOption.text
    ) {
        DropDownEnumListItem(
            selected = intentHandlingOption,
            enabled = enabled,
            onSelect = {
                ConfigurationSettings.intentHandlingOption.unsaved.value = it
            })
        { IntentHandlingOptions.values() }


        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentHandlingEndpoint.observeCurrent(),
                enabled = enabled,
                onValueChange = { ConfigurationSettings.intentHandlingEndpoint.unsaved.value = it },
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
                    enabled = enabled,
                    onValueChange = { ConfigurationSettings.intentHandlingHassUrl.unsaved.value = it },
                    label = MR.strings.hassURL
                )

                var isShowAccessToken by rememberSaveable { mutableStateOf(false) }

                TextFieldListItem(
                    value = ConfigurationSettings.intentHandlingHassAccessToken.observeCurrent(),
                    onValueChange = { ConfigurationSettings.intentHandlingHassAccessToken.unsaved.value = it },
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
                    enabled = enabled,
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsaved.value = true
                    })

                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    enabled = enabled,
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsaved.value = false
                    })
            }
        }
    }
}