package org.rhasspy.mobile.viewmodel.navigation.destinations

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

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