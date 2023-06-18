package org.rhasspy.mobile.viewmodel.navigation.destinations

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType

sealed interface ConfigurationScreenNavigationDestination : NavigationDestination {

    val destinationType: ConfigurationScreenDestinationType

    data class AudioPlayingConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class DialogManagementConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class IntentHandlingConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class IntentRecognitionConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class MqttConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class RemoteHermesHttpConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class SpeechToTextConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class TextToSpeechConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class WakeWordConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination
    data class WebServerConfigurationScreen(override val destinationType: ConfigurationScreenDestinationType): ConfigurationScreenNavigationDestination

}