package org.rhasspy.mobile.settings

enum class SettingsEnum {
    //App Settings
    LanguageOption,
    ThemeOption,

    AutomaticSilenceDetection,

    BackgroundWakeWordDetection,
    BackgroundWakeWordDetectionTurnOnDisplay,

    WakeWordSoundIndication,
    WakeWordLightIndication,

    ShowLog,

    //Configuration Settings
    SiteId,

    HttpSSL,

    MQTT_SSL,
    MQTTHost,
    MQTTPort,
    MQTTUserName,
    MQTTPassword,

    UDPOutput,
    UDPOutputHost,
    UDPOutputPort,

    WakeWordOption,
    WakeWordAccessToken,
    WakeWordKeyword,

    SpeechToTextOption,
    SpeechToTextHttpEndpoint,

    IntentRecognitionOption,
    IntentRecognitionEndpoint,

    TextToSpeechOption,
    TextToSpeechEndpoint,

    AudioPlayingOption,
    AudioPlayingEndpoint,

    DialogueManagementOption,

    IntentHandlingOption,
    IntentHandlingEndpoint,
    IntentHandlingHassUrl,
    IntentHandlingHassAccessToken,
    IsIntentHandlingHassEvent
}