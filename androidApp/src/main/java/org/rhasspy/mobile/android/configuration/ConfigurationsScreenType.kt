package org.rhasspy.mobile.android.configuration

/**
 * enum to hold all possible configuration screens
 */
enum class ConfigurationScreenType(val route: String) {
    AudioPlayingConfiguration("ConfigurationScreenType_AudioPlayingConfiguration"),
    DialogManagementConfiguration("ConfigurationScreenType_DialogManagementConfiguration"),
    IntentHandlingConfiguration("ConfigurationScreenType_IntentHandlingConfiguration"),
    IntentRecognitionConfiguration("ConfigurationScreenType_IntentRecognitionConfiguration"),
    MqttConfiguration("ConfigurationScreenType_MqttConfiguration"),
    RemoteHermesHttpConfiguration("ConfigurationScreenType_RemoteHermesHttpConfiguration"),
    SpeechToTextConfiguration("ConfigurationScreenType_SpeechToTextConfiguration"),
    TextToSpeechConfiguration("ConfigurationScreenType_TextToSpeechConfiguration"),
    WakeWordConfiguration("ConfigurationScreenType_WakeWordConfiguration"),
    WebServerConfiguration("ConfigurationScreenType_WebServerConfiguration")
}