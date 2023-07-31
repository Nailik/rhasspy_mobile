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

        OverviewScreen,
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

        OverviewScreen,
        AboutSettings,
        AudioFocusSettings,
        AudioRecorderSettings,
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

        OverviewScreen,
        EditPorcupineLanguageScreen,
        EditPorcupineWakeWordScreen

    }

}