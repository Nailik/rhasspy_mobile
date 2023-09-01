package org.rhasspy.mobile.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.ui.configuration.*
import org.rhasspy.mobile.ui.configuration.connection.HttpConnectionDetailScreenWrapper
import org.rhasspy.mobile.ui.configuration.connection.HttpConnectionListScreen
import org.rhasspy.mobile.ui.configuration.connection.MqttConnectionScreen
import org.rhasspy.mobile.ui.configuration.connection.WebServerConnectionScreen
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.ui.configuration.speechtotext.SpeechToTextAudioOutputFormatScreen
import org.rhasspy.mobile.ui.configuration.speechtotext.SpeechToTextAudioRecorderFormatScreen
import org.rhasspy.mobile.ui.configuration.wakeword.WakeWordAudioOutputFormatScreen
import org.rhasspy.mobile.ui.configuration.wakeword.WakeWordAudioRecorderFormatScreen
import org.rhasspy.mobile.ui.settings.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.*

@Composable
fun NavigationContent(
    screen: NavigationDestination
) {
    Crossfade(targetState = screen) {
        when (it) {
            is ConfigurationScreenNavigationDestination   -> ConfigurationNavigationContent(it)
            is ConnectionScreenNavigationDestination      -> ConnectionScreenNavigationContent(it)
            is MainScreenNavigationDestination            -> MainNavigationContent(it)
            is SettingsScreenDestination                  -> SettingsNavigationContent(it)
            is WakeWordConfigurationScreenDestination     -> WakeWordNavigationContent(it)
            is IndicationSettingsScreenDestination        -> IndicationNavigationContent(it)
            is SpeechToTextConfigurationScreenDestination -> SpeechToTextConfigurationNavigationContent(it)
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
                MainScreenNavigationDestination.HomeScreen          -> HomeScreen()
                MainScreenNavigationDestination.DialogScreen        -> DialogScreen()
                MainScreenNavigationDestination.ConfigurationScreen -> ConfigurationScreen()
                MainScreenNavigationDestination.SettingsScreen      -> SettingsScreen()
                MainScreenNavigationDestination.LogScreen           -> LogScreen()
            }
        }

        BottomNavigation()
    }
}

@Composable
private fun ConfigurationNavigationContent(
    screen: ConfigurationScreenNavigationDestination
) {
    when (screen) {
        ConfigurationScreenNavigationDestination.ConnectionsConfigurationScreen            -> ConnectionsConfigurationScreen()
        ConfigurationScreenNavigationDestination.DialogManagementConfigurationScreen       -> DialogManagementConfigurationScreen()
        ConfigurationScreenNavigationDestination.AudioInputConfigurationScreen             -> AudioInputConfigurationScreen()
        ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen               -> WakeWordConfigurationOverviewScreen()
        ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen           -> SpeechToTextConfigurationScreen()
        ConfigurationScreenNavigationDestination.VoiceActivityDetectionConfigurationScreen -> VoiceActivityDetectionConfigurationScreen()
        ConfigurationScreenNavigationDestination.IntentRecognitionConfigurationScreen      -> IntentRecognitionConfigurationScreen()
        ConfigurationScreenNavigationDestination.IntentHandlingConfigurationScreen         -> IntentHandlingConfigurationScreen()
        ConfigurationScreenNavigationDestination.TextToSpeechConfigurationScreen           -> TextToSpeechConfigurationScreen()
        ConfigurationScreenNavigationDestination.AudioPlayingConfigurationScreen           -> AudioPlayingConfigurationScreen()
    }
}

@Composable
private fun ConnectionScreenNavigationContent(
    screen: ConnectionScreenNavigationDestination
) {
    when (screen) {
        ConnectionScreenNavigationDestination.MqttConnectionScreen          -> MqttConnectionScreen()
        ConnectionScreenNavigationDestination.HttpConnectionListScreen      -> HttpConnectionListScreen()
        ConnectionScreenNavigationDestination.WebServerConnectionScreen     -> WebServerConnectionScreen()
        is ConnectionScreenNavigationDestination.HttpConnectionDetailScreen -> HttpConnectionDetailScreenWrapper(screen.viewModel)
    }
}

@Composable
private fun SettingsNavigationContent(
    screen: SettingsScreenDestination
) {
    when (screen) {
        SettingsScreenDestination.AboutSettings             -> AboutScreen()
        SettingsScreenDestination.AudioFocusSettings        -> AudioFocusSettingsContent()
        SettingsScreenDestination.BackgroundServiceSettings -> BackgroundServiceSettingsContent()
        SettingsScreenDestination.DeviceSettings            -> DeviceSettingsContent()
        SettingsScreenDestination.IndicationSettings        -> IndicationSettingsOverviewScreen()
        SettingsScreenDestination.AppearanceSettingsScreen  -> AppearanceSettingsScreenItemContent()
        SettingsScreenDestination.LogSettings               -> LogSettingsContent()
        SettingsScreenDestination.MicrophoneOverlaySettings -> MicrophoneOverlaySettingsContent()
        SettingsScreenDestination.SaveAndRestoreSettings    -> SaveAndRestoreSettingsContent()
    }
}

@Composable
private fun WakeWordNavigationContent(
    screen: WakeWordConfigurationScreenDestination
) {
    when (screen) {
        WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen -> PorcupineLanguageScreen()
        WakeWordConfigurationScreenDestination.EditPorcupineWakeWordScreen -> PorcupineKeywordScreen()
        WakeWordConfigurationScreenDestination.AudioRecorderFormatScreen   -> WakeWordAudioRecorderFormatScreen()
        WakeWordConfigurationScreenDestination.AudioOutputFormatScreen     -> WakeWordAudioOutputFormatScreen()
    }
}

@Composable
private fun SpeechToTextConfigurationNavigationContent(
    screen: SpeechToTextConfigurationScreenDestination
) {
    when (screen) {
        SpeechToTextConfigurationScreenDestination.AudioRecorderFormatScreen -> SpeechToTextAudioRecorderFormatScreen()
        SpeechToTextConfigurationScreenDestination.AudioOutputFormatScreen   -> SpeechToTextAudioOutputFormatScreen()
    }
}

@Composable
private fun IndicationNavigationContent(
    screen: IndicationSettingsScreenDestination
) {
    when (screen) {
        IndicationSettingsScreenDestination.WakeIndicationSoundScreen     -> IndicationWakeScreen()
        IndicationSettingsScreenDestination.RecordedIndicationSoundScreen -> IndicationRecordedScreen()
        IndicationSettingsScreenDestination.ErrorIndicationSoundScreen    -> IndicationErrorScreen()
    }
}


