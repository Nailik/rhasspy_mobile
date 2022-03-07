package org.rhasspy.mobile.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.viewModels.SettingsData

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

    DropDownEnumListItem(
        selected = SettingsData.languageOption.observe(),
        onSelect = { SettingsData.languageOption.value = it })
    { LanguageOptions.values() }

}

@Composable
fun ThemeItem() {

    DropDownEnumListItem(
        selected = SettingsData.themeOption.observe(),
        onSelect = { SettingsData.themeOption.value = it })
    { ThemeOptions.values() }

}

@Composable
fun AutomaticSilenceDetectionItem() {

    SwitchListItem(
        text = MR.strings.automaticSilenceDetection,
        isChecked = SettingsData.automaticSilenceDetection.observe(),
        onCheckedChange = { SettingsData.automaticSilenceDetection.value = !SettingsData.automaticSilenceDetection.value })

}

@Composable
fun BackgroundWakeWordDetectionItem() {

    val isBackgroundWakeWordDetection = SettingsData.isBackgroundWakeWordDetection.observe()

    ExpandableListItem(
        text = MR.strings.backgroundWakeWordDetection,
        secondaryText = isBackgroundWakeWordDetection.toText()
    ) {
        Column {
            SwitchListItem(
                text = MR.strings.enableBackgroundWakeWordDetection,
                isChecked = isBackgroundWakeWordDetection,
                onCheckedChange = { SettingsData.isBackgroundWakeWordDetection.value = it })

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = SettingsData.isBackgroundWakeWordDetectionTurnOnDisplay.observe(),
                onCheckedChange = { SettingsData.isBackgroundWakeWordDetectionTurnOnDisplay.value = it })
        }
    }
}


@Composable
fun WakeWordIndicationItem() {
    val isWakeWordSoundIndication = SettingsData.isWakeWordSoundIndication.observe()
    val isWakeWordLightIndication = SettingsData.isWakeWordLightIndication.observe()


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
                onCheckedChange = { SettingsData.isWakeWordSoundIndication.value = it })

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = { SettingsData.isWakeWordLightIndication.value = it })
        }
    }
}

@Composable
fun ShowLogItem() {

    SwitchListItem(MR.strings.showLog,
        isChecked = SettingsData.isShowLog.observe(),
        onCheckedChange = { SettingsData.isShowLog.value = it })

}