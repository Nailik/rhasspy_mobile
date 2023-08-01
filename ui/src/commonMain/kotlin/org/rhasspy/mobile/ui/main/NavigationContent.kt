package org.rhasspy.mobile.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreen
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.*
import org.rhasspy.mobile.ui.configuration.*
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.*

@Composable
fun NavigationContent(
    screen: NavigationDestination,
    bottomNavigation: @Composable () -> Unit
) {
    //only animates when the type changes
    Crossfade(targetState = screen) {
        when (it) {
            is ConfigurationScreenNavigationDestination -> ConfigurationNavigationContent(it)
            is MainScreenNavigationDestination          -> MainNavigationContent(it, bottomNavigation)
            is SettingsScreenDestination                -> SettingsNavigationContent(it)
            is WakeWordConfigurationScreenDestination   -> WakeWordNavigationContent(it)
            is IndicationSettingsScreenDestination      -> IndicationNavigationContent(it)
        }
    }
}


@Composable
private fun MainNavigationContent(
    screen: MainScreenNavigationDestination,
    bottomNavigation: @Composable () -> Unit
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

        bottomNavigation()
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
        SettingsScreenDestination.AudioRecorderSettings     -> AudioRecorderSettingsContent()
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