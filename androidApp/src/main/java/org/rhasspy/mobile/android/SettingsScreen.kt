package org.rhasspy.mobile.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions

@Preview
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LanguageItem()
        Divider()
        ThemeItem()
        Divider()
        AutomaticSilenceDetectionItem()
        Divider()
        BackgroundWakeWordDetectionItem()
        Divider()
        WakeWordIndicationItem()
        Divider()
        ShowLogItem()
        Divider()
    }
}

@Composable
fun LanguageItem() {
    var languageValue by remember { mutableStateOf(LanguageOptions.English) }

    DropDownEnumListItem(languageValue, onSelect = { languageValue = it }) { LanguageOptions.values() }
}

@Composable
fun ThemeItem() {
    var themeValue by remember { mutableStateOf(ThemeOptions.System) }

    DropDownEnumListItem(themeValue, onSelect = { themeValue = it }) { ThemeOptions.values() }
}

@Composable
fun AutomaticSilenceDetectionItem() {
    var checked by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable { checked = !checked },
        text = { Text(MR.strings.automaticSilenceDetection) },
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = { checked = !checked })
        })
}

@Composable
fun BackgroundWakeWordDetectionItem() {
    var isEnableBackgroundWakeWordDetection by remember { mutableStateOf(false) }
    var isBackgroundWakeWordDetectionTurnOnDisplay by remember { mutableStateOf(false) }

    ExpandableListItem(
        text = MR.strings.backgroundWakeWordDetection,
        secondaryText = isEnableBackgroundWakeWordDetection.toText()
    ) {
        Column {
            SwitchListItem(
                text = MR.strings.enableBackgroundWakeWordDetection,
                isChecked = isEnableBackgroundWakeWordDetection,
                onCheckedChange = { isEnableBackgroundWakeWordDetection = it })

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = isBackgroundWakeWordDetectionTurnOnDisplay,
                onCheckedChange = { isBackgroundWakeWordDetectionTurnOnDisplay = it })
        }
    }
}


@Composable
fun WakeWordIndicationItem() {
    var isWakeWordSoundIndication by remember { mutableStateOf(false) }
    var isWakeWordLightIndication by remember { mutableStateOf(false) }

    var stateText = if (isWakeWordSoundIndication) translate(MR.strings.sound) else null
    if (isWakeWordLightIndication) {
        if (!stateText.isNullOrEmpty()) {
            stateText += " " + translate(MR.strings._and)
        }
        stateText += translate(MR.strings.light)
    }

    ExpandableListItemString(
        text = MR.strings.wakeWordIndication,
        secondaryText = stateText
    ) {

        Column {
            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = isWakeWordSoundIndication,
                onCheckedChange = { isWakeWordSoundIndication = it })

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = { isWakeWordLightIndication = it })
        }
    }
}

@Composable
fun ShowLogItem() {
    var isShowLog by remember { mutableStateOf(false) }

    SwitchListItem(MR.strings.showLog,
        isChecked = isShowLog,
        onCheckedChange = { isShowLog = it })
}