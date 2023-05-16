package org.rhasspy.mobile.viewmodel.navigation.destinations

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

enum class ConfigurationScreenDestination : NavigationDestination {

    OverviewScreen,
    AudioPlayingConfigurationScreenDestination,
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