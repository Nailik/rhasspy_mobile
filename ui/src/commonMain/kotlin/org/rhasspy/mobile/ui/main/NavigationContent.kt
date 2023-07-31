package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.*
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.theme.horizontalAnimationSpec
import org.rhasspy.mobile.ui.configuration.*
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.ui.main.ConfigurationScreen
import org.rhasspy.mobile.ui.main.DialogScreen
import org.rhasspy.mobile.ui.main.HomeScreen
import org.rhasspy.mobile.ui.main.LogScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.*

@Composable
fun NavigationContent(
    screen: NavigationDestination
) {
    AnimatedContent(targetState = screen) { targetState ->
        when (screen) {
            is ConfigurationScreenNavigationDestination       -> ConfigurationNavigationContent(screen)
            is MainScreenNavigationDestination                -> MainNavigationContent(screen)
            is PorcupineKeywordConfigurationScreenDestination -> PorcupineNavigationContent(screen)
            is SettingsScreenDestination                      -> SettingsNavigationContent(screen)
            is WakeWordConfigurationScreenDestination         -> WakeWordNavigationContent(screen)
        }
    }
}

@Composable
private fun ConfigurationNavigationContent(
    screen: ConfigurationScreenNavigationDestination
) {
    AnimatedContent(targetState = screen) { targetState ->
        when (targetState) {
            ConfigurationScreenNavigationDestination.OverviewScreen                       -> ConfigurationScreen()
            ConfigurationScreenNavigationDestination.AudioPlayingConfigurationScreen      -> AudioPlayingConfigurationScreen()
            ConfigurationScreenNavigationDestination.DialogManagementConfigurationScreen  -> DialogManagementConfigurationScreen()
            ConfigurationScreenNavigationDestination.IntentHandlingConfigurationScreen    -> IntentHandlingConfigurationScreen()
            ConfigurationScreenNavigationDestination.IntentRecognitionConfigurationScreen -> IntentRecognitionConfigurationScreen()
            ConfigurationScreenNavigationDestination.MqttConfigurationScreen              -> MqttConfigurationScreen()
            ConfigurationScreenNavigationDestination.RemoteHermesHttpConfigurationScreen  -> RemoteHermesHttpConfigurationScreen()
            ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen      -> SpeechToTextConfigurationScreen()
            ConfigurationScreenNavigationDestination.TextToSpeechConfigurationScreen      -> TextToSpeechConfigurationScreen()
            ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen          -> WakeWordConfigurationScreen()
            ConfigurationScreenNavigationDestination.WebServerConfigurationScreen         -> WebServerConfigurationScreen()
        }
    }
}


@Composable
private fun MainNavigationContent(
    screen: MainScreenNavigationDestination
) {
    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            horizontalAnimationSpec(targetState.ordinal, initialState.ordinal)
        }
    ) {
        when (screen) {
            MainScreenNavigationDestination.HomeScreen          -> HomeScreen()
            MainScreenNavigationDestination.DialogScreen        -> DialogScreen()
            MainScreenNavigationDestination.ConfigurationScreen -> ConfigurationScreen()
            MainScreenNavigationDestination.SettingsScreen      -> SettingsScreen()
            MainScreenNavigationDestination.LogScreen           -> LogScreen()
        }
    }
}

@Composable
private fun SettingsNavigationContent(
    screen: SettingsScreenDestination
) {
    AnimatedContent(targetState = screen) { targetState ->
        when (targetState) {
            SettingsScreenDestination.OverviewScreen            -> SettingsScreen()
            SettingsScreenDestination.AboutSettings             -> AboutScreen()
            SettingsScreenDestination.AudioFocusSettings        -> AudioFocusSettingsContent()
            SettingsScreenDestination.AudioRecorderSettings     -> AudioRecorderSettingsContent()
            SettingsScreenDestination.SilenceDetectionSettings  -> SilenceDetectionSettingsContent()
            SettingsScreenDestination.BackgroundServiceSettings -> BackgroundServiceSettingsContent()
            SettingsScreenDestination.DeviceSettings            -> DeviceSettingsContent()
            SettingsScreenDestination.IndicationSettings        -> IndicationSettingsContent()
            SettingsScreenDestination.LanguageSettingsScreen    -> LanguageSettingsScreenItemContent()
            SettingsScreenDestination.LogSettings               -> LogSettingsContent()
            SettingsScreenDestination.MicrophoneOverlaySettings -> MicrophoneOverlaySettingsContent()
            SettingsScreenDestination.SaveAndRestoreSettings    -> SaveAndRestoreSettingsContent()
        }
    }
}

@Composable
private fun WakeWordNavigationContent(
    screen: WakeWordConfigurationScreenDestination
) {
    AnimatedContent(targetState = screen) { targetState ->
        when (targetState) {
            WakeWordConfigurationScreenDestination.OverviewScreen              -> WakeWordConfigurationOverviewScreen()
            WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen -> PorcupineLanguageScreen()
            WakeWordConfigurationScreenDestination.EditPorcupineWakeWordScreen -> PorcupineKeywordScreen()
        }
    }
}


