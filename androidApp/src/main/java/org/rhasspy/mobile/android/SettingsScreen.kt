package org.rhasspy.mobile.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Preview
@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LanguageItem(viewModel)
        Divider()
        ThemeItem(viewModel)
        Divider()
        AutomaticSilenceDetectionItem(viewModel)
        Divider()
        BackgroundWakeWordDetectionItem(viewModel)
        Divider()
        WakeWordIndicationItem(viewModel)
        Divider()
        ShowLogItem(viewModel)
        Divider()
    }
}

@Composable
fun LanguageItem(viewModel: SettingsScreenViewModel) {

    DropDownEnumListItem(
        selected = viewModel.languageOption.observe(),
        onSelect = { viewModel.languageOption.value = it })
    { LanguageOptions.values() }

}

@Composable
fun ThemeItem(viewModel: SettingsScreenViewModel) {

    DropDownEnumListItem(
        selected = viewModel.themeOption.observe(),
        onSelect = { viewModel.themeOption.value = it })
    { ThemeOptions.values() }

}

@Composable
fun AutomaticSilenceDetectionItem(viewModel: SettingsScreenViewModel) {

    SwitchListItem(
        text = MR.strings.automaticSilenceDetection,
        isChecked = viewModel.automaticSilenceDetection.observe(),
        onCheckedChange = { viewModel.automaticSilenceDetection.value = !viewModel.automaticSilenceDetection.value })

}

@Composable
fun BackgroundWakeWordDetectionItem(viewModel: SettingsScreenViewModel) {

    val isBackgroundWakeWordDetection = viewModel.isBackgroundWakeWordDetection.observe()

    ExpandableListItem(
        text = MR.strings.backgroundWakeWordDetection,
        secondaryText = isBackgroundWakeWordDetection.toText()
    ) {
        Column {
            SwitchListItem(
                text = MR.strings.enableBackgroundWakeWordDetection,
                isChecked = isBackgroundWakeWordDetection,
                onCheckedChange = { viewModel.isBackgroundWakeWordDetection.value = it })

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isBackgroundWakeWordDetectionTurnOnDisplay.observe(),
                onCheckedChange = { viewModel.isBackgroundWakeWordDetectionTurnOnDisplay.value = it })
        }
    }
}


@Composable
fun WakeWordIndicationItem(viewModel: SettingsScreenViewModel) {
    val isWakeWordSoundIndication = viewModel.isWakeWordSoundIndication.observe()
    val isWakeWordLightIndication = viewModel.isWakeWordLightIndication.observe()


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
                onCheckedChange = { viewModel.isWakeWordSoundIndication.value = it })

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = { viewModel.isWakeWordLightIndication.value = it })
        }
    }
}

@Composable
fun ShowLogItem(viewModel: SettingsScreenViewModel) {

    SwitchListItem(MR.strings.showLog,
        isChecked = viewModel.isShowLog.observe(),
        onCheckedChange = { viewModel.isShowLog.value = it })

}