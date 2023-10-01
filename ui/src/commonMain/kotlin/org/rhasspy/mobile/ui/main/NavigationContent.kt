package org.rhasspy.mobile.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.ui.configuration.ConnectionsConfigurationScreen
import org.rhasspy.mobile.ui.configuration.DialogManagementConfigurationScreen
import org.rhasspy.mobile.ui.configuration.connections.*
import org.rhasspy.mobile.ui.configuration.domains.asr.SpeechToTextConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.handle.IntentHandlingConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.intent.IntentRecognitionConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.mic.AudioInputConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.mic.AudioOutputFormatConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.mic.AudioRecorderFormatConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.snd.AudioPlayingConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.tts.TextToSpeechConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.vad.VoiceActivityDetectionConfigurationScreen
import org.rhasspy.mobile.ui.configuration.domains.wake.WakeWordConfigurationOverviewScreen
import org.rhasspy.mobile.ui.configuration.domains.wake.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.domains.wake.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.ui.settings.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.*

@Composable
fun NavigationContent(
    screen: NavigationDestination
) {
    LaunchedEffect(screen) {
        screen.viewModel.onVisible()
    }

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
            Crossfade(targetState = screen) {
                when (screen) {
                    is MainScreenNavigationDestination.HomeScreen          -> HomeScreen(screen.viewModel)
                    is MainScreenNavigationDestination.DialogScreen        -> DialogScreen(screen.viewModel)
                    is MainScreenNavigationDestination.ConfigurationScreen -> ConfigurationScreen(screen.viewModel)
                    is MainScreenNavigationDestination.SettingsScreen      -> SettingsScreen(screen.viewModel)
                    is MainScreenNavigationDestination.LogScreen           -> LogScreen(screen.viewModel)
                }
            }
        }

        BottomNavigation(screen.bottomViewModel)
    }
}

@Composable
private fun ConfigurationNavigationContent(
    screen: ConfigurationScreenNavigationDestination
) {
    Crossfade(targetState = screen) {
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
}

@Composable
private fun ConnectionScreenNavigationContent(
    screen: ConnectionScreenNavigationDestination
) {
    Crossfade(targetState = screen) {
        when (screen) {
            is ConnectionScreenNavigationDestination.MqttConnectionScreen            -> MqttConnectionScreen(screen.viewModel)
            is ConnectionScreenNavigationDestination.HomeAssistantConnectionScreen   -> HomeAssistantConnectionScreen(screen.viewModel)
            is ConnectionScreenNavigationDestination.Rhasspy2HermesConnectionScreen  -> Rhasspy2HermesConnectionScreen(screen.viewModel)
            is ConnectionScreenNavigationDestination.Rhasspy3WyomingConnectionScreen -> Rhasspy3WyomingConnectionScreen(screen.viewModel)
            is ConnectionScreenNavigationDestination.WebServerConnectionScreen       -> WebServerConnectionScreen(screen.viewModel)
        }
    }
}

@Composable
private fun SettingsNavigationContent(
    screen: SettingsScreenDestination
) {
    Crossfade(targetState = screen) {
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
}

@Composable
private fun WakeWordNavigationContent(
    screen: WakeWordConfigurationScreenDestination
) {
    Crossfade(targetState = screen) {
        when (screen) {
            is WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen -> PorcupineLanguageScreen(screen.viewModel)
            is WakeWordConfigurationScreenDestination.EditPorcupineWakeWordScreen -> PorcupineKeywordScreen(screen.viewModel)
        }
    }
}

@Composable
private fun AudioInputDomainNavigationContent(
    screen: AudioInputDomainScreenDestination
) {
    Crossfade(targetState = screen) {
        when (screen) {
            is AudioInputDomainScreenDestination.AudioInputFormatScreen -> AudioRecorderFormatConfigurationScreen(screen.viewModel)
            is AudioInputDomainScreenDestination.AudioOutputFormatScreen -> AudioOutputFormatConfigurationScreen(screen.viewModel)
        }
    }
}

@Composable
private fun IndicationNavigationContent(
    screen: IndicationSettingsScreenDestination
) {
    Crossfade(targetState = screen) {
        when (screen) {
            is IndicationSettingsScreenDestination.WakeIndicationSoundScreen     -> IndicationWakeScreen(screen.viewModel)
            is IndicationSettingsScreenDestination.RecordedIndicationSoundScreen -> IndicationRecordedScreen(screen.viewModel)
            is IndicationSettingsScreenDestination.ErrorIndicationSoundScreen    -> IndicationErrorScreen(screen.viewModel)
        }
    }
}


