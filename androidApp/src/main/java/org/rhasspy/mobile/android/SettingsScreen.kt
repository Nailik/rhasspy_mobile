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
import org.rhasspy.mobile.settings.AppSettings

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
        BackgroundWakeWordDetectionItem()
        Divider()
        WakeWordIndicationItem()
        Divider()
        AutomaticSilenceDetectionItem()
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
fun AutomaticSilenceDetectionItem() {

    SwitchListItem(
        text = MR.strings.automaticSilenceDetection,
        isChecked = AppSettings.automaticSilenceDetection.observe(),
        onCheckedChange = { AppSettings.automaticSilenceDetection.data = !AppSettings.automaticSilenceDetection.data })

}

@Composable
fun BackgroundWakeWordDetectionItem() {

    SwitchListItem(
        text = MR.strings.enableBackground,
        isChecked = AppSettings.isBackgroundEnabled.observe(),
        onCheckedChange = { AppSettings.isBackgroundEnabled.data = it })

}


@Composable
fun WakeWordIndicationItem() {
    val isWakeWordSoundIndication = AppSettings.isWakeWordSoundIndication.observe()
    val isWakeWordLightIndication = AppSettings.isWakeWordLightIndication.observe()


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
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.observe(),
                onCheckedChange = { AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data = it })

            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = isWakeWordSoundIndication,
                onCheckedChange = { AppSettings.isWakeWordSoundIndication.data = it })

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = { AppSettings.isWakeWordLightIndication.data = it })
        }
    }
}

@Composable
fun ShowLogItem() {

    SwitchListItem(MR.strings.showLog,
        isChecked = AppSettings.isShowLog.observe(),
        onCheckedChange = { AppSettings.isShowLog.data = it })

}