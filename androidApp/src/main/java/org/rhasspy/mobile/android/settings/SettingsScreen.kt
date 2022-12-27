package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.icerock.moko.resources.StringResource
import org.koin.androidx.compose.get
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.*
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.settings.content.*
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.screens.SettingsScreenViewModel


@Preview
@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = get()) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.settings) }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            item {
                Language(viewModel)
                CustomDivider()
            }

            item {
                BackgroundService(viewModel)
                CustomDivider()
            }

            item {
                MicrophoneOverlay(viewModel)
                CustomDivider()
            }

            item {
                Indication(viewModel)
                CustomDivider()
            }

            item {
                Device()
                CustomDivider()
            }

            item {
                AutomaticSilenceDetection(viewModel)
                CustomDivider()
            }

            item {
                Log(viewModel)
                CustomDivider()
            }

            item {
                SaveAndRestore()
                CustomDivider()
            }

            item {
                About()
                CustomDivider()
            }

        }
    }

}

/**
 * configuration screens with list items that open bottom sheet
 */
fun NavGraphBuilder.addSettingsScreen() {

    composable(SettingsScreenType.LanguageSettings.name) {
        LanguageSettingsScreenItemContent()
    }

    composable(SettingsScreenType.BackgroundServiceSettings.name) {
        BackgroundServiceSettingsContent()
    }

    composable(SettingsScreenType.MicrophoneOverlaySettings.name) {
        MicrophoneOverlaySettingsContent()
    }

    composable(SettingsScreenType.IndicationSettings.name) {
        WakeWordIndicationSettingsContent()
    }

    composable(SettingsScreenType.DeviceSettings.name) {
        DeviceSettingsContent()
    }

    composable(SettingsScreenType.AutomaticSilenceDetectionSettings.name) {
        AutomaticSilenceDetectionSettingsContent()
    }

    composable(SettingsScreenType.LogSettings.name) {
        LogSettingsContent()
    }

    composable(SettingsScreenType.SaveAndRestoreSettings.name) {
        SaveAndRestoreSettingsContent()
    }

    composable(SettingsScreenType.AboutSettings.name) {
        AboutScreen()
    }

}

@Composable
private fun Language(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.language,
        secondaryText = viewModel.currentLanguage.collectAsState().value.text,
        screen = SettingsScreenType.LanguageSettings
    )

}

@Composable
private fun BackgroundService(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.background,
        secondaryText = viewModel.isBackgroundEnabled.collectAsState().value.toText(),
        screen = SettingsScreenType.BackgroundServiceSettings
    )

}

@Composable
private fun MicrophoneOverlay(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.microphoneOverlay,
        secondaryText = viewModel.microphoneOverlaySizeOption.collectAsState().value.name,
        screen = SettingsScreenType.MicrophoneOverlaySettings
    )

}

@Composable
private fun Indication(viewModel: SettingsScreenViewModel) {

    val isWakeWordSoundIndication by viewModel.isSoundIndicationEnabled.collectAsState()
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
        text = MR.strings.indication,
        secondaryText = stateText,
        screen = SettingsScreenType.IndicationSettings
    )

}

@Composable
private fun Device() {

    SettingsListItem(
        text = MR.strings.device,
        secondaryText = MR.strings.deviceSettingsInformation,
        screen = SettingsScreenType.DeviceSettings
    )

}

@Composable
private fun AutomaticSilenceDetection(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.automaticSilenceDetection,
        secondaryText = viewModel.isAutomaticSilenceDetectionEnabled.collectAsState().value.toText(),
        screen = SettingsScreenType.AutomaticSilenceDetectionSettings
    )

}

@Composable
private fun Log(viewModel: SettingsScreenViewModel) {

    SettingsListItem(
        text = MR.strings.logSettings,
        secondaryText = viewModel.logLevel.collectAsState().value.text,
        screen = SettingsScreenType.LogSettings
    )

}

@Composable
private fun SaveAndRestore() {

    SettingsListItem(
        text = MR.strings.saveAndRestoreSettings,
        secondaryText = MR.strings.backup,
        screen = SettingsScreenType.SaveAndRestoreSettings
    )

}

@Composable
private fun About() {

    SettingsListItem(
        text = MR.strings.aboutTitle,
        secondaryText = "${translate(MR.strings.version)} ${BuildKonfig.versionName}",
        screen = SettingsScreenType.AboutSettings
    )

}


@Composable
private fun SettingsListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    screen: SettingsScreenType
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
    @Suppress("SameParameterValue") screen: SettingsScreenType
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