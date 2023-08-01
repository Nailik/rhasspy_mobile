package org.rhasspy.mobile.viewmodel.navigation

sealed interface NavigationDestination {

    enum class MainScreenNavigationDestination : NavigationDestination {

        HomeScreen,
        DialogScreen,
        ConfigurationScreen,
        SettingsScreen,
        LogScreen

    }

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

    enum class WakeWordConfigurationScreenDestination : NavigationDestination {

        EditPorcupineLanguageScreen,
        EditPorcupineWakeWordScreen,
        AudioRecorderFormatScreen,
        AudioOutputFormatScreen

    }

    enum class SpeechToTextConfigurationScreenDestination : NavigationDestination {

        AudioRecorderFormatScreen,
        AudioOutputFormatScreen,

    }

    enum class IndicationSettingsScreenDestination : NavigationDestination {

        WakeIndicationSoundScreen,
        RecordedIndicationSoundScreen,
        ErrorIndicationSoundScreen

    }

}