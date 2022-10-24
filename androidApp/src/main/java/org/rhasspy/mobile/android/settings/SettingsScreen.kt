package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.settings.content.AboutSettingsContent
import org.rhasspy.mobile.android.settings.content.AutomaticSilenceDetectionSettingsContent
import org.rhasspy.mobile.android.settings.content.BackgroundServiceSettingsContent
import org.rhasspy.mobile.android.settings.content.DeviceSettingsContent
import org.rhasspy.mobile.android.settings.content.LogSettingsContent
import org.rhasspy.mobile.android.settings.content.MicrophoneOverlaySettingsContent
import org.rhasspy.mobile.android.settings.content.ProblemHandlingSettingsContent
import org.rhasspy.mobile.android.settings.content.SaveAndRestoreSettingsContent
import org.rhasspy.mobile.android.settings.content.SoundsSettingsContent
import org.rhasspy.mobile.android.settings.content.WakeWordIndicationSettingsContent
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

enum class SettingsScreens {
    SettingsList,
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

/**
 * configuration screens with list items that open bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SettingsAppBar(scrollBehavior)
            },
        ) { paddingValues ->

            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = SettingsScreens.SettingsList.name
            ) {

                composable(SettingsScreens.SettingsList.name) {
                    SettingsList()
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
                    AboutSettingsContent()
                }

            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    var currentDestination by remember { mutableStateOf(SettingsScreens.SettingsList) }
    val navController = LocalNavController.current

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = SettingsScreens.valueOf(destination.route ?: SettingsScreens.SettingsList.name)
        }
    }

    val title = when (currentDestination) {
        SettingsScreens.SettingsList -> MR.strings.settings
        SettingsScreens.BackgroundServiceSettings -> MR.strings.background
        SettingsScreens.MicrophoneOverlaySettings -> MR.strings.microphoneOverlay
        SettingsScreens.WakeWordIndicationSettings -> MR.strings.wakeWordIndication
        SettingsScreens.SoundsSettings -> MR.strings.sounds
        SettingsScreens.DeviceSettings -> MR.strings.device
        SettingsScreens.AutomaticSilenceDetectionSettings -> MR.strings.automaticSilenceDetection
        SettingsScreens.LogSettings -> MR.strings.logSettings
        SettingsScreens.ProblemHandlingSettings -> MR.strings.problemHandling
        SettingsScreens.SaveAndRestoreSettings -> MR.strings.saveAndRestoreSettings
        SettingsScreens.AboutSettings -> MR.strings.aboutTitle
    }

    MediumTopAppBar(
        title = {
            Text(title)
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * screen with list that open settings bottom sheet
 */
@Composable
fun SettingsList(viewModel: SettingsScreenViewModel = viewModel()) {

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

        AboutSettingsContent()
        CustomDivider()

    }

}

@Composable
private fun Language(viewModel: SettingsScreenViewModel) {

    DropDownEnumListItem(
        selected = viewModel.currentLanguage.collectAsState().value,
        values = viewModel.languageOptions,
        onSelect = viewModel::selectLanguage
    )

}

@Composable
private fun Theme(viewModel: SettingsScreenViewModel) {

    DropDownEnumListItem(
        selected = viewModel.currentTheme.collectAsState().value,
        values = viewModel.themeOptions,
        onSelect = viewModel::selectTheme
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
private fun SettingsListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    screen: SettingsScreens
) {
    val navController = LocalNavController.current

    ListElement(
        modifier = Modifier.clickable {
            navController.navigate(screen.name)
        },
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
    val navController = LocalNavController.current

    ListElement(
        modifier = Modifier.clickable {
            navController.navigate(screen.name)
        },
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}