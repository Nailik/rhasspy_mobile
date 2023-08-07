package org.rhasspy.mobile.viewmodel.navigation

import androidx.compose.runtime.Stable

@Stable
sealed interface NavigationDestination {

    @Stable
    enum class MainScreenNavigationDestination : NavigationDestination {

        HomeScreen,
        DialogScreen,
        ConfigurationScreen,
        SettingsScreen,
        LogScreen

    }

    @Stable
    enum class ConfigurationScreenNavigationDestination : NavigationDestination {

        AudioPlayingConfigurationScreen,
        DialogManagementConfigurationScreen,
        IntentHandlingConfigurationScreen,
        IntentRecognitionConfigurationScreen,
        MqttConfigurationScreen,
        RemoteHermesHttpConfigurationScreen,
        SpeechToTextConfigurationScreen,
        TextToSpeechConfigurationScreen,
        WakeWordConfigurationScreen,
        WebServerConfigurationScreen

    }

    @Stable
    enum class SettingsScreenDestination : NavigationDestination {

        AboutSettings,
        AudioFocusSettings,
        SilenceDetectionSettings,
        BackgroundServiceSettings,
        DeviceSettings,
        IndicationSettings,
        LanguageSettingsScreen,
        LogSettings,
        MicrophoneOverlaySettings,
        SaveAndRestoreSettings

    }

    @Stable
    enum class WakeWordConfigurationScreenDestination : NavigationDestination {

        EditPorcupineLanguageScreen,
        EditPorcupineWakeWordScreen,
        AudioRecorderFormatScreen,
        AudioOutputFormatScreen

    }

    @Stable
    enum class SpeechToTextConfigurationScreenDestination : NavigationDestination {

        AudioRecorderFormatScreen,
        AudioOutputFormatScreen,

    }

    @Stable
    enum class IndicationSettingsScreenDestination : NavigationDestination {

        WakeIndicationSoundScreen,
        RecordedIndicationSoundScreen,
        ErrorIndicationSoundScreen

    }

}