package org.rhasspy.mobile.android.bottomBarScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.navigation.MainScreens
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
fun SettingsScreen(snackbarHostState: SnackbarHostState, mainNavController: NavController, viewModel: SettingsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LanguageItem()
        CustomDivider()
        ThemeItem()
        CustomDivider()
        BackgroundService(viewModel)
        CustomDivider()
        MicrophoneOverlay()
        CustomDivider()
        WakeWordIndicationItem()
        CustomDivider()
        Sounds(viewModel)
        CustomDivider()
        Device()
        CustomDivider()
        AutomaticSilenceDetectionItem(viewModel, snackbarHostState)
        CustomDivider()
        ShowLogItem()
        CustomDivider()
        ProblemHandling()
        CustomDivider()
        SaveAndRestore(viewModel)
        CustomDivider()
        About(mainNavController)
    }
}

@Composable
fun LanguageItem() {

    DropDownEnumListItem(
        selected = AppSettings.languageOption.data.collectAsState().value,
        onSelect = { AppSettings.languageOption.value = it })
    { LanguageOptions.values() }

}

@Composable
fun ThemeItem() {

    DropDownEnumListItem(
        selected = AppSettings.themeOption.data.collectAsState().value,
        onSelect = { AppSettings.themeOption.value = it })
    { ThemeOptions.values() }

}

@Composable
fun AutomaticSilenceDetectionItem(viewModel: SettingsScreenViewModel, snackbarHostState: SnackbarHostState) {

    val isAutomaticSilenceDetection = AppSettings.isAutomaticSilenceDetection.data.collectAsState().value

    ExpandableListItem(
        text = MR.strings.automaticSilenceDetection,
        secondaryText = isAutomaticSilenceDetection.toText()
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.automaticSilenceDetection,
                isChecked = isAutomaticSilenceDetection,
                onCheckedChange = { AppSettings.isAutomaticSilenceDetection.value = !AppSettings.isAutomaticSilenceDetection.value })

            androidx.compose.animation.AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isAutomaticSilenceDetection
            ) {

                Column {

                    TextFieldListItem(
                        label = MR.strings.silenceDetectionTime,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = AppSettings.automaticSilenceDetectionTime.data.collectAsState().value.toString(),
                        onValueChange = {
                            val integer = it.replace("-", "")
                                .replace(",", "")
                                .replace(".", "")
                                .replace(" ", "")
                                .toIntOrNull()

                            logger.v { "parsed automaticSilenceDetectionTime to $integer" }

                            AppSettings.automaticSilenceDetectionTime.value = integer ?: 0
                        },
                    )


                    TextFieldListItem(
                        label = MR.strings.audioLevelThreshold,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = AppSettings.automaticSilenceDetectionAudioLevel.data.collectAsState().value.toString(),
                        onValueChange = {
                            val integer = it.replace("-", "")
                                .replace(",", "")
                                .replace(".", "")
                                .replace(" ", "")
                                .toIntOrNull()

                            logger.v { "parsed automaticSilenceDetectionAudioLevel to $integer" }

                            AppSettings.automaticSilenceDetectionAudioLevel.value = integer ?: 0
                        },
                    )


                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {

                        val status = viewModel.status.collectAsState().value
                        val audioLevel = viewModel.audioLevel.collectAsState().value

                        val animatedWeight = animateFloatAsState(targetValue = if (status) 1f else 0f)
                        val animatedColor = animateColorAsState(
                            targetValue = if (audioLevel > AppSettings.automaticSilenceDetectionAudioLevel.data.collectAsState().value) {
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
fun BackgroundService(viewModel: SettingsScreenViewModel) {

    val isBackgroundEnabled = AppSettings.isBackgroundEnabled.data.collectAsState().value

    ExpandableListItem(
        text = MR.strings.background,
        secondaryText = isBackgroundEnabled.toText()
    ) {
        //on oFF
        SwitchListItem(
            text = MR.strings.enableBackground,
            isChecked = isBackgroundEnabled,
            onCheckedChange = {
                AppSettings.isBackgroundEnabled.value = it
            })

        var isBatteryDisabled by rememberSaveable { mutableStateOf(false) }

        ComposableLifecycle { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isBatteryDisabled = viewModel.isBatteryOptimizationDisabled()
            }
        }

        //background battery optimization on/off
        ListElement(
            modifier = Modifier.clickable { viewModel.onDisableBatteryOptimization() },
            text = { Text(MR.strings.batteryOptimization) },
            secondaryText = { Text(isBatteryDisabled.toText()) },
            trailing = {
                Icon(
                    imageVector = Icons.Filled.BatteryAlert,
                    contentDescription = MR.strings.batteryOptimization
                )
            }
        )
    }
}


@Composable
fun MicrophoneOverlay() {

    val isMicrophoneOverlayEnabled = AppSettings.isMicrophoneOverlayEnabled.data.collectAsState().value

    ExpandableListItem(
        text = MR.strings.microphoneOverlay,
        secondaryText = isMicrophoneOverlayEnabled.toText()
    ) {
        val requestOverlayPermission = requestOverlayPermission {
            if (it) {
                AppSettings.isMicrophoneOverlayEnabled.value = true
            }
        }

        Column {
            SwitchListItem(
                text = MR.strings.showMicrophoneOverlay,
                secondaryText = MR.strings.showMicrophoneOverlayInfo,
                isChecked = isMicrophoneOverlayEnabled,
                onCheckedChange = {
                    if (it && !OverlayPermission.granted.value) {
                        requestOverlayPermission.invoke()
                    } else {
                        AppSettings.isMicrophoneOverlayEnabled.value = it
                    }
                })

            SwitchListItem(
                text = MR.strings.whileAppIsOpened,
                isChecked = AppSettings.isMicrophoneOverlayWhileApp.data.collectAsState().value,
                onCheckedChange = {
                    AppSettings.isMicrophoneOverlayWhileApp.value = it
                })

        }
    }
}

@Composable
fun Device() {

    ExpandableListItem(
        text = MR.strings.device,
        secondaryText = MR.strings.deviceSettingsInformation
    ) {
        Column {
            SliderListItem(
                text = MR.strings.volume,
                value = AppSettings.volume.data.collectAsState().value,
                onValueChange = {
                    AppSettings.volume.value = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
                })

            SwitchListItem(
                text = MR.strings.hotWord,
                isChecked = AppSettings.isHotWordEnabled.data.collectAsState().value,
                onCheckedChange = { AppSettings.isHotWordEnabled.value = it })

            SwitchListItem(
                text = MR.strings.audioOutput,
                isChecked = AppSettings.isAudioOutputEnabled.data.collectAsState().value,
                onCheckedChange = { AppSettings.isAudioOutputEnabled.value = it })


            SwitchListItem(
                text = MR.strings.intentHandling,
                isChecked = AppSettings.isIntentHandlingEnabled.data.collectAsState().value,
                onCheckedChange = { AppSettings.isIntentHandlingEnabled.value = it })


        }

    }
}

@Composable
fun Sounds(viewModel: SettingsScreenViewModel) {

    ExpandableListItem(
        text = MR.strings.sounds,
        secondaryText = MR.strings.soundsText
    ) {
        Column {
            SliderListItem(
                text = MR.strings.volume,
                value = AppSettings.soundVolume.data.collectAsState().value,
                onValueChange = {
                    AppSettings.soundVolume.value = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
                })

            val allSounds = AppSettings.customSounds.data.collectAsState().value.toTypedArray().addSoundItems()

            DropDownStringList(
                overlineText = { Text(MR.strings.wakeSound) },
                selected = AppSettings.wakeSound.data.collectAsState().value,
                values = allSounds,
            ) {
                viewModel.selectWakeSoundFile(it)
            }

            DropDownStringList(
                overlineText = { Text(MR.strings.recordedSound) },
                selected = AppSettings.recordedSound.data.collectAsState().value,
                values = allSounds
            ) {
                viewModel.selectRecordedSoundFile(it)
            }

            DropDownStringList(
                overlineText = { Text(MR.strings.errorSound) },
                selected = AppSettings.errorSound.data.collectAsState().value,
                values = allSounds
            ) {
                viewModel.selectErrorSoundFile(it)
            }

            CustomSoundFile(viewModel)
        }

    }
}

@Composable
private fun CustomSoundFile(viewModel: SettingsScreenViewModel) {
    DropDownListRemovableWithFileOpen(
        overlineText = { Text(MR.strings.sounds) },
        title = { Text(MR.strings.selectCustomSoundFile) },
        values = viewModel.customSoundValuesUi.collectAsState().value,
        onAdd = {
            viewModel.selectCustomSoundFile()
        },
        onRemove = {
            viewModel.removeCustomSoundFile(it)
        })
}

@Composable
private fun Array<String>.addSoundItems(): Array<String> {
    return this.toMutableList().apply {
        this.addAll(0, listOf(translate(resource = MR.strings.defaultText), translate(resource = MR.strings.disabled)))
    }.toTypedArray()
}

@Composable
fun WakeWordIndicationItem() {
    val isWakeWordSoundIndication = AppSettings.isWakeWordSoundIndication.data.collectAsState().value
    val isWakeWordLightIndication = AppSettings.isWakeWordLightIndication.data.collectAsState().value

    var stateText = if (isWakeWordSoundIndication) translate(MR.strings.sound) else ""
    if (isWakeWordLightIndication) {
        if (stateText.isNotEmpty()) {
            stateText += " ${translate(MR.strings._and)} "
        }
        stateText += translate(MR.strings.light)
    }
    if (stateText.isEmpty()) {
        stateText = translate(MR.strings.disabled)
    }

    ExpandableListItemString(
        text = MR.strings.wakeWordIndication,
        secondaryText = stateText
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data.collectAsState().value,
                onCheckedChange = { AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.value = it })

            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = isWakeWordSoundIndication,
                onCheckedChange = { AppSettings.isWakeWordSoundIndication.value = it })


            val requestOverlayPermission = requestOverlayPermission {
                if (it) {
                    AppSettings.isWakeWordLightIndication.value = true
                }
            }

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = {
                    if (it && !OverlayPermission.granted.value) {
                        requestOverlayPermission.invoke()
                    } else {
                        AppSettings.isWakeWordLightIndication.value = it
                    }
                })
        }
    }
}

@Composable
fun ShowLogItem() {
    val logLevel = AppSettings.logLevel.data.collectAsState().value

    ExpandableListItem(
        text = MR.strings.logSettings,
        secondaryText = logLevel.text
    ) {
        DropDownEnumListItem(
            selected = logLevel,
            onSelect = { AppSettings.logLevel.value = it })
        { LogLevel.values() }

        SwitchListItem(MR.strings.showLog,
            isChecked = AppSettings.isShowLog.data.collectAsState().value,
            onCheckedChange = { AppSettings.isShowLog.value = it })

        SwitchListItem(MR.strings.audioFramesLogging,
            isChecked = AppSettings.isLogAudioFrames.data.collectAsState().value,
            onCheckedChange = { AppSettings.isLogAudioFrames.value = it })
    }
}

@Composable
fun ProblemHandling() {
    ExpandableListItem(
        text = MR.strings.problemHandling
    ) {
        SwitchListItem(
            text = MR.strings.forceCancel,
            secondaryText = MR.strings.forceCancelInformation,
            isChecked = AppSettings.isForceCancelEnabled.data.collectAsState().value,
            onCheckedChange = { AppSettings.isForceCancelEnabled.value = it })
    }
}

@Composable
fun SaveAndRestore(viewModel: SettingsScreenViewModel) {
    ExpandableListItem(
        text = MR.strings.saveAndRestoreSettings
    ) {
        val openSaveSettingsDialog = remember { mutableStateOf(false) }
        val openRestoreSettingsDialog = remember { mutableStateOf(false) }

        ListElement(
            modifier = Modifier
            .clickable { openSaveSettingsDialog.value = true },
            text = { Text(MR.strings.save) },
            secondaryText = { Text(MR.strings.saveText) })

        ListElement(modifier = Modifier
            .clickable { openRestoreSettingsDialog.value = true },
            text = { Text(MR.strings.restore) },
            secondaryText = { Text(MR.strings.restoreText) })

        if (openSaveSettingsDialog.value) {
            SaveSettingsDialog {
                openSaveSettingsDialog.value = false
                if (it) {
                    viewModel.saveSettingsFile()
                }
            }
        }

        if (openRestoreSettingsDialog.value) {
            RestoreSettingsDialog {
                openRestoreSettingsDialog.value = false
                if (it) {
                    viewModel.restoreSettingsFromFile()
                }
            }
        }
    }
}

@Composable
fun SaveSettingsDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.saveSettings) },
        text = { Text(MR.strings.saveSettingsText, textAlign = TextAlign.Center) },
        icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = MR.strings.warning) },
        confirmButton = {
            Button(onClick = { onResult.invoke(true) }) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onResult.invoke(false) }) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Composable
fun RestoreSettingsDialog(onResult: (result: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult.invoke(false) },
        title = { Text(MR.strings.restoreSettings) },
        text = { Text(MR.strings.restoreSettingsText, textAlign = TextAlign.Center) },
        icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = MR.strings.warning) },
        confirmButton = {
            Button(onClick = { onResult.invoke(true) }) {
                Text(MR.strings.ok)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onResult.invoke(false) }) {
                Text(MR.strings.cancel)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Composable
fun About(mainNavController: NavController) {
    ListElement(
        modifier = Modifier.clickable {
            mainNavController.navigate(MainScreens.AboutScreen.name)
        },
        icon = { Icon(Icons.Filled.Info, modifier = Modifier.size(24.dp), contentDescription = MR.strings.info) },
        text = { Text(MR.strings.aboutTitle) },
        secondaryText = { Text("${translate(MR.strings.version)} ${BuildKonfig.versionName} - ${BuildKonfig.versionCode}") }
    )
}