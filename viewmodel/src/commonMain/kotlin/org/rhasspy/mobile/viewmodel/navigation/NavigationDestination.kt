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

        ConnectionsConfigurationScreen,
        DialogManagementConfigurationScreen,
        AudioInputConfigurationScreen,
        WakeWordConfigurationScreen,
        SpeechToTextConfigurationScreen,
        VoiceActivityDetectionConfigurationScreen,
        IntentRecognitionConfigurationScreen,
        IntentHandlingConfigurationScreen,
        TextToSpeechConfigurationScreen,
        AudioPlayingConfigurationScreen,

    }

    @Stable
    enum class ConnectionScreenNavigationDestination : NavigationDestination {

        MqttConnectionScreen,
        RemoteHermesHttpConnectionScreen,
        WebServerConnectionScreen

    }

    @Stable
    enum class SettingsScreenDestination : NavigationDestination {

        AboutSettings,
        AudioFocusSettings,
        BackgroundServiceSettings,
        DeviceSettings,
        IndicationSettings,
        AppearanceSettingsScreen,
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