package org.rhasspy.mobile.android.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.permissions.requestOverlayPermission
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel
import java.math.RoundingMode

private val logger = Logger.withTag("SettingsScreen")

@Composable
fun SettingsScreen(snackbarHostState: SnackbarHostState, viewModel: SettingsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LanguageItem()
        Divider()
        ThemeItem()
        Divider()
        BackgroundService()
        Divider()
        Volume()
        Divider()
        WakeWordIndicationItem()
        Divider()
        AutomaticSilenceDetectionItem(viewModel, snackbarHostState)
        Divider()
        ShowLogItem()
        Divider()
    }
}

@Composable
fun LanguageItem() {

    DropDownEnumListItem(
        selected = AppSettings.languageOption.observe(),
        onSelect = { AppSettings.languageOption.data = it })
    { LanguageOptions.values() }

}

@Composable
fun ThemeItem() {

    DropDownEnumListItem(
        selected = AppSettings.themeOption.observe(),
        onSelect = { AppSettings.themeOption.data = it })
    { ThemeOptions.values() }

}

@Composable
fun AutomaticSilenceDetectionItem(viewModel: SettingsScreenViewModel, snackbarHostState: SnackbarHostState) {

    val isAutomaticSilenceDetection = AppSettings.isAutomaticSilenceDetection.observe()

    ExpandableListItem(
        text = MR.strings.automaticSilenceDetection,
        secondaryText = isAutomaticSilenceDetection.toText()
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.automaticSilenceDetection,
                isChecked = isAutomaticSilenceDetection,
                onCheckedChange = { AppSettings.isAutomaticSilenceDetection.data = !AppSettings.isAutomaticSilenceDetection.data })

            androidx.compose.animation.AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isAutomaticSilenceDetection
            ) {

                Column {

                    TextFieldListItem(
                        label = MR.strings.silenceDetectionTime,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = AppSettings.automaticSilenceDetectionTime.observe().toString(),
                        onValueChange = {
                            val integer = it.replace("-", "")
                                .replace(",", "")
                                .replace(".", "")
                                .replace(" ", "")
                                .toIntOrNull()

                            logger.v { "parsed automaticSilenceDetectionTime to $integer" }

                            AppSettings.automaticSilenceDetectionTime.data = integer ?: 0
                        },
                    )


                    TextFieldListItem(
                        label = MR.strings.audioLevelThreshold,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = AppSettings.automaticSilenceDetectionAudioLevel.observe().toString(),
                        onValueChange = {
                            val integer = it.replace("-", "")
                                .replace(",", "")
                                .replace(".", "")
                                .replace(" ", "")
                                .toIntOrNull()

                            logger.v { "parsed automaticSilenceDetectionAudioLevel to $integer" }

                            AppSettings.automaticSilenceDetectionAudioLevel.data = integer ?: 0
                        },
                    )


                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {

                        val status = viewModel.status.observe()
                        val audioLevel = viewModel.audioLevel.observe()

                        val animatedWeight = animateFloatAsState(targetValue = if (status) 1f else 0f)
                        val animatedColor = animateColorAsState(
                            targetValue = if (audioLevel > AppSettings.automaticSilenceDetectionAudioLevel.observe()) {
                                MaterialTheme.colorScheme.error
                            } else MaterialTheme.colorScheme.primary
                        )

                        if (animatedWeight.value > 0f) {

                            OutlinedButton(
                                modifier = Modifier
                                    .weight(animatedWeight.value)
                                    .alpha(animatedWeight.value)
                                    .padding(end = 16.dp)
                                    .fillMaxWidth(),
                                border = BorderStroke(ButtonDefaults.outlinedButtonBorder.width, animatedColor.value),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = animatedColor.value),
                                onClick = { })
                            {
                                Text(audioLevel.toString())
                            }
                        }


                        val requestMicrophonePermission =
                            requestMicrophonePermission(snackbarHostState, MR.strings.microphonePermissionInfoAudioLevel) {
                                if (it) {
                                    viewModel.toggleAudioLevelTest()
                                }
                            }

                        Button(
                            modifier = Modifier
                                .weight(2f - animatedWeight.value)
                                .wrapContentSize(),
                            onClick = { requestMicrophonePermission.invoke() }) {
                            Icon(if (status) Icons.Filled.MicOff else Icons.Filled.Mic, MR.strings.microphone)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (status) MR.strings.stop else MR.strings.testAudioLevel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundService() {

    SwitchListItem(
        text = MR.strings.enableBackground,
        isChecked = AppSettings.isBackgroundEnabled.observe(),
        onCheckedChange = { AppSettings.isBackgroundEnabled.data = it })

}

@Composable
fun Volume() {

    SliderListItem(
        text = MR.strings.volume,
        value = AppSettings.volume.observe(),
        onValueChange = {
            AppSettings.volume.data = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
        })

}

@Composable
fun WakeWordIndicationItem() {
    val isWakeWordSoundIndication = AppSettings.isWakeWordSoundIndication.observe()
    val isWakeWordLightIndication = AppSettings.isWakeWordLightIndication.observe()

    var stateText = if (isWakeWordSoundIndication) translate(MR.strings.sound) else ""
    if (isWakeWordLightIndication) {
        if (stateText.isNotEmpty()) {
            stateText += " ${translate(MR.strings._and)} "
        }
        stateText += translate(MR.strings.light)
    }

    ExpandableListItemString(
        text = MR.strings.wakeWordIndication,
        secondaryText = stateText.ifEmpty { null }
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.observe(),
                onCheckedChange = { AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data = it })

            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = isWakeWordSoundIndication,
                onCheckedChange = { AppSettings.isWakeWordSoundIndication.data = it })


            val requestOverlayPermission = requestOverlayPermission {
                if (it) {
                    AppSettings.isWakeWordLightIndication.data = true
                }
            }

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = {
                    if (it && !OverlayPermission.granted.value) {
                        requestOverlayPermission.invoke()
                    } else {
                        AppSettings.isWakeWordLightIndication.data = it
                    }
                })
        }
    }
}

@Composable
fun ShowLogItem() {
    val logLevel = AppSettings.logLevel.observe()

    ExpandableListItem(
        text = MR.strings.logSettings,
        secondaryText = logLevel.text
    ) {
        DropDownEnumListItem(
            selected = logLevel,
            onSelect = { AppSettings.logLevel.data = it })
        { LogLevel.values() }

        SwitchListItem(MR.strings.showLog,
            isChecked = AppSettings.isShowLog.observe(),
            onCheckedChange = { AppSettings.isShowLog.data = it })
    }
}