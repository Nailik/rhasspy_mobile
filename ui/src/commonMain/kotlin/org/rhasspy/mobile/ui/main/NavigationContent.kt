package org.rhasspy.mobile.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.ui.configuration.*
import org.rhasspy.mobile.ui.configuration.audioinput.AudioInputFormatConfigurationScreen
import org.rhasspy.mobile.ui.configuration.audioinput.AudioOutputFormatConfigurationScreen
import org.rhasspy.mobile.ui.configuration.connection.*
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.ui.settings.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.*

@Composable
fun NavigationContent(
    screen: NavigationDestination
) {
    Crossfade(targetState = screen) {
        when (it) {
            is ConfigurationScreenNavigationDestination -> ConfigurationNavigationContent(it)
            is ConnectionScreenNavigationDestination    -> ConnectionScreenNavigationContent(it)
            is MainScreenNavigationDestination          -> MainNavigationContent(it)
            is SettingsScreenDestination                -> SettingsNavigationContent(it)
            is WakeWordConfigurationScreenDestination   -> WakeWordNavigationContent(it)
            is IndicationSettingsScreenDestination      -> IndicationNavigationContent(it)
            is AudioInputDomainScreenDestination        -> AudioInputDomainNavigationContent(it)
        }
    }
}


@Composable
private fun MainNavigationContent(
    screen: MainScreenNavigationDestination
) {
    Column {
        Box(modifier = Modifier.weight(1f)) {
            when (screen) {
                is MainScreenNavigationDestination.HomeScreen          -> HomeScreen(screen.viewModel)
                is MainScreenNavigationDestination.DialogScreen        -> DialogScreen(screen.viewModel)
                is MainScreenNavigationDestination.ConfigurationScreen -> ConfigurationScreen(screen.viewModel)
                is MainScreenNavigationDestination.SettingsScreen      -> SettingsScreen(screen.viewModel)
                is MainScreenNavigationDestination.LogScreen           -> LogScreen(screen.viewModel)
            }
        }

        BottomNavigation(screen.bottomViewModel)
    }
}

@Composable
private fun ConfigurationNavigationContent(
    screen: ConfigurationScreenNavigationDestination
) {
    when (screen) {
        is ConfigurationScreenNavigationDestination.ConnectionsConfigurationScreen            -> ConnectionsConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.DialogManagementConfigurationScreen       -> DialogManagementConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.AudioInputConfigurationScreen             -> AudioInputConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen               -> WakeWordConfigurationOverviewScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen           -> SpeechToTextConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.VoiceActivityDetectionConfigurationScreen -> VoiceActivityDetectionConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.IntentRecognitionConfigurationScreen      -> IntentRecognitionConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.IntentHandlingConfigurationScreen         -> IntentHandlingConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.TextToSpeechConfigurationScreen           -> TextToSpeechConfigurationScreen(screen.viewModel)
        is ConfigurationScreenNavigationDestination.AudioPlayingConfigurationScreen           -> AudioPlayingConfigurationScreen(screen.viewModel)
    }
}

@Composable
private fun ConnectionScreenNavigationContent(
    screen: ConnectionScreenNavigationDestination
) {
    when (screen) {
        is ConnectionScreenNavigationDestination.MqttConnectionScreen            -> MqttConnectionScreen(screen.viewModel)
        is ConnectionScreenNavigationDestination.HomeAssistantConnectionScreen   -> HomeAssistantConnectionScreen(screen.viewModel)
        is ConnectionScreenNavigationDestination.Rhasspy2HermesConnectionScreen  -> Rhasspy2HermesConnectionScreen(screen.viewModel)
        is ConnectionScreenNavigationDestination.Rhasspy3WyomingConnectionScreen -> Rhasspy3WyomingConnectionScreen(screen.viewModel)
        is ConnectionScreenNavigationDestination.WebServerConnectionScreen       -> WebServerConnectionScreen(screen.viewModel)
    }
}

@Composable
private fun SettingsNavigationContent(
    screen: SettingsScreenDestination
) {
    when (screen) {
        is SettingsScreenDestination.AboutSettings             -> AboutScreen(screen.viewModel)
        is SettingsScreenDestination.AudioFocusSettings        -> AudioFocusSettingsContent(screen.viewModel)
        is SettingsScreenDestination.BackgroundServiceSettings -> BackgroundServiceSettingsContent(screen.viewModel)
        is SettingsScreenDestination.DeviceSettings            -> DeviceSettingsContent(screen.viewModel)
        is SettingsScreenDestination.IndicationSettings        -> IndicationSettingsOverviewScreen(screen.viewModel)
        is SettingsScreenDestination.AppearanceSettingsScreen  -> AppearanceSettingsScreenItemContent(screen.viewModel)
        is SettingsScreenDestination.LogSettings               -> LogSettingsContent(screen.viewModel)
        is SettingsScreenDestination.MicrophoneOverlaySettings -> MicrophoneOverlaySettingsContent(screen.viewModel)
        is SettingsScreenDestination.SaveAndRestoreSettings    -> SaveAndRestoreSettingsContent(screen.viewModel)
    }
}

@Composable
private fun WakeWordNavigationContent(
    screen: WakeWordConfigurationScreenDestination
) {
    when (screen) {
        is WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen -> PorcupineLanguageScreen(screen.viewModel)
        is WakeWordConfigurationScreenDestination.EditPorcupineWakeWordScreen -> PorcupineKeywordScreen(screen.viewModel)
    }
}

@Composable
private fun AudioInputDomainNavigationContent(
    screen: AudioInputDomainScreenDestination
) {
    when (screen) {
        is AudioInputDomainScreenDestination.AudioInputFormatScreen  -> AudioInputFormatConfigurationScreen(screen.viewModel)
        is AudioInputDomainScreenDestination.AudioOutputFormatScreen -> AudioOutputFormatConfigurationScreen(screen.viewModel)
    }
}

@Composable
private fun IndicationNavigationContent(
    screen: IndicationSettingsScreenDestination
) {
    when (screen) {
        is IndicationSettingsScreenDestination.WakeIndicationSoundScreen     -> IndicationWakeScreen(screen.viewModel)
        is IndicationSettingsScreenDestination.RecordedIndicationSoundScreen -> IndicationRecordedScreen(screen.viewModel)
        is IndicationSettingsScreenDestination.ErrorIndicationSoundScreen    -> IndicationErrorScreen(screen.viewModel)
    }
}


