package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.settings.content.*
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

enum class SettingsScreens {
    LanguageSettings,
    ThemeSettings,
    BackgroundServiceSettings,
    MicrophoneOverlaySettings,
    WakeWordIndicationSettings,
    SoundsSettings,
    DeviceSettings,
    AutomaticSilenceDetectionSettings,
    LogSettings,
    ProblemHandlingSettings,
    SaveAndRestoreSettings,
    AboutSettings
}

@Preview
@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Language(viewModel)
        CustomDivider()

        Theme(viewModel)
        CustomDivider()

        BackgroundService(viewModel)
        CustomDivider()

        MicrophoneOverlay(viewModel)
        CustomDivider()

        WakeWordIndication(viewModel)
        CustomDivider()

        Sounds()
        CustomDivider()

        Device()
        CustomDivider()

        AutomaticSilenceDetection(viewModel)
        CustomDivider()

        Log(viewModel)
        CustomDivider()

        ProblemHandling(viewModel)
        CustomDivider()

        SaveAndRestore()
        CustomDivider()

        About()
        CustomDivider()

    }

}

/**
 * configuration screens with list items that open bottom sheet
 */
fun NavGraphBuilder.addSettingsScreen() {

    composable(SettingsScreens.LanguageSettings.name) {
        LanguageSettingsScreenItemContent()
    }

    composable(SettingsScreens.ThemeSettings.name) {
        ThemeSettingsScreenItemContent()
    }

    composable(SettingsScreens.BackgroundServiceSettings.name) {
        BackgroundServiceSettingsContent()
    }

    composable(SettingsScreens.MicrophoneOverlaySettings.name) {
        MicrophoneOverlaySettingsContent()
    }

    composable(SettingsScreens.WakeWordIndicationSettings.name) {
        WakeWordIndicationSettingsContent()
    }

    composable(SettingsScreens.SoundsSettings.name) {
        SoundsSettingsContent()
    }

    composable(SettingsScreens.DeviceSettings.name) {
        DeviceSettingsContent()
    }

    composable(SettingsScreens.AutomaticSilenceDetectionSettings.name) {
        AutomaticSilenceDetectionSettingsContent()
    }

    composable(SettingsScreens.LogSettings.name) {
        LogSettingsContent()
    }

    composable(SettingsScreens.ProblemHandlingSettings.name) {
        ProblemHandlingSettingsContent()
    }

    composable(SettingsScreens.SaveAndRestoreSettings.name) {
        SaveAndRestoreSettingsContent()
    }

    composable(SettingsScreens.AboutSettings.name) {
        AboutScreen()
    }

}

@Composable
private fun Language(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.language,
        secondaryText = viewModel.currentLanguage.collectAsState().value.text,
        screen = SettingsScreens.LanguageSettings
    )

}

@Composable
private fun Theme(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.theme,
        secondaryText = viewModel.currentTheme.collectAsState().value.text,
        screen = SettingsScreens.ThemeSettings
    )

}

@Composable
private fun BackgroundService(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.background,
        secondaryText = viewModel.isBackgroundEnabled.collectAsState().value.toText(),
        screen = SettingsScreens.BackgroundServiceSettings
    )

}

@Composable
private fun MicrophoneOverlay(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.microphoneOverlay,
        secondaryText = viewModel.isMicrophoneOverlayEnabled.collectAsState().value.toText(),
        screen = SettingsScreens.MicrophoneOverlaySettings
    )

}

@Composable
private fun WakeWordIndication(viewModel: SettingsScreenViewModel) {

    val isWakeWordSoundIndication by viewModel.isWakeWordSoundIndicationEnabled.collectAsState()
    val isWakeWordLightIndication by viewModel.isWakeWordLightIndicationEnabled.collectAsState()

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

    SettingsListItem(
        text = MR.strings.wakeWordIndication,
        secondaryText = stateText,
        screen = SettingsScreens.WakeWordIndicationSettings
    )

}

@Composable
private fun Sounds() {

    SettingsListItem(
        text = MR.strings.sounds,
        secondaryText = MR.strings.soundsText,
        screen = SettingsScreens.SoundsSettings
    )

}

@Composable
private fun Device() {

    SettingsListItem(
        text = MR.strings.device,
        secondaryText = MR.strings.deviceSettingsInformation,
        screen = SettingsScreens.DeviceSettings
    )

}

@Composable
private fun AutomaticSilenceDetection(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.automaticSilenceDetection,
        secondaryText = viewModel.isAutomaticSilenceDetectionEnabled.collectAsState().value.toText(),
        screen = SettingsScreens.AutomaticSilenceDetectionSettings
    )

}

@Composable
private fun Log(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.logSettings,
        secondaryText = viewModel.logLevel.collectAsState().value.text,
        screen = SettingsScreens.LogSettings
    )

}

@Composable
private fun ProblemHandling(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.problemHandling,
        secondaryText = viewModel.isForceCancelEnabled.collectAsState().value.toText(),
        screen = SettingsScreens.ProblemHandlingSettings
    )

}

@Composable
private fun SaveAndRestore() {

    SettingsListItem(
        text = MR.strings.saveAndRestoreSettings,
        secondaryText = MR.strings.backup,
        screen = SettingsScreens.SaveAndRestoreSettings
    )

}

@Composable
private fun About() {

    SettingsListItem(
        text = MR.strings.aboutTitle,
        secondaryText = "${translate(MR.strings.version)} ${BuildKonfig.versionName}",
        screen = SettingsScreens.AboutSettings
    )

}



@Composable
private fun SettingsListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    screen: SettingsScreens
) {
    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { secondaryText?.also { Text(secondaryText) } }
    )
}

@Composable
private fun SettingsListItem(
    text: StringResource,
    secondaryText: String,
    @Suppress("SameParameterValue") screen: SettingsScreens
) {
    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}