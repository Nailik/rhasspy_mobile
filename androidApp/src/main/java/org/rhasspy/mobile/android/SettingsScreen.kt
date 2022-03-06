package org.rhasspy.mobile.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.Language
import org.rhasspy.mobile.data.Theme

@Preview
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
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
    DropDownEnumListItem(Language.English) { Language.values() }
}

@Composable
fun ThemeItem() {
    DropDownEnumListItem(Theme.System) { Theme.values() }
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
    ExpandableListItem(text = MR.strings.backgroundWakeWordDetection) {
        Column {
            SwitchListItem(MR.strings.enableBackgroundWakeWordDetection)
            SwitchListItem(MR.strings.backgroundWakeWordDetectionTurnOnDisplay)
        }
    }
}


@Composable
fun WakeWordIndicationItem() {
    ExpandableListItem(text = MR.strings.wakeWordIndication) {
        Column {
            SwitchListItem(MR.strings.wakeWordSoundIndication)
            SwitchListItem(MR.strings.wakeWordLightIndication)
        }
    }
}

@Composable
fun ShowLogItem() {
    SwitchListItem(MR.strings.showLog)
}