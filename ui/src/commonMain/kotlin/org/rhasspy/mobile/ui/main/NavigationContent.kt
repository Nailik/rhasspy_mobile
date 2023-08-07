package org.rhasspy.mobile.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.ui.configuration.*
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
    //only animates when the type changes
    Crossfade(targetState = screen) {
        when (screen) {
            is ConfigurationScreenNavigationDestination   -> ConfigurationNavigationContent(screen)
            is MainScreenNavigationDestination            -> MainNavigationContent(screen)
            is SettingsScreenDestination                  -> SettingsNavigationContent(screen)
            is WakeWordConfigurationScreenDestination     -> WakeWordNavigationContent(screen)
            is IndicationSettingsScreenDestination        -> IndicationNavigationContent(screen)
            is SpeechToTextConfigurationScreenDestination -> SpeechToTextConfigurationNavigationContent(screen)
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
        ConfigurationScreenNavigationDestination.AudioPlayingConfigurationScreen      -> AudioPlayingConfigurationScreen()
        ConfigurationScreenNavigationDestination.DialogManagementConfigurationScreen  -> DialogManagementConfigurationScreen()
        ConfigurationScreenNavigationDestination.IntentHandlingConfigurationScreen    -> IntentHandlingConfigurationScreen()
        ConfigurationScreenNavigationDestination.IntentRecognitionConfigurationScreen -> IntentRecognitionConfigurationScreen()
        ConfigurationScreenNavigationDestination.MqttConfigurationScreen              -> MqttConfigurationScreen()
        ConfigurationScreenNavigationDestination.RemoteHermesHttpConfigurationScreen  -> RemoteHermesHttpConfigurationScreen()
        ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen      -> SpeechToTextConfigurationScreen()
        ConfigurationScreenNavigationDestination.TextToSpeechConfigurationScreen      -> TextToSpeechConfigurationScreen()
        ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen          -> WakeWordConfigurationOverviewScreen()
        ConfigurationScreenNavigationDestination.WebServerConfigurationScreen         -> WebServerConfigurationScreen()
    }
}


@Composable
private fun SettingsNavigationContent(
    screen: SettingsScreenDestination
) {
    when (screen) {
        SettingsScreenDestination.AboutSettings             -> AboutScreen()
        SettingsScreenDestination.AudioFocusSettings        -> AudioFocusSettingsContent()
        SettingsScreenDestination.SilenceDetectionSettings  -> SilenceDetectionSettingsContent()
        SettingsScreenDestination.BackgroundServiceSettings -> BackgroundServiceSettingsContent()
        SettingsScreenDestination.DeviceSettings            -> DeviceSettingsContent()
        SettingsScreenDestination.IndicationSettings        -> IndicationSettingsOverviewScreen()
        SettingsScreenDestination.LanguageSettingsScreen    -> LanguageSettingsScreenItemContent()
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
        IndicationSettingsScreenDestination.WakeIndicationSoundScreen     -> IndicationErrorScreen()
        IndicationSettingsScreenDestination.RecordedIndicationSoundScreen -> IndicationRecordedScreen()
        IndicationSettingsScreenDestination.ErrorIndicationSoundScreen    -> IndicationWakeScreen()
    }
}


