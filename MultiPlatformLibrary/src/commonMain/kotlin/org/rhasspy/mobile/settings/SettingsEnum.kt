package org.rhasspy.mobile.settings

enum class SettingsEnum {
    //App Settings
    LanguageOption,
    ThemeOption,

    AutomaticSilenceDetection,
    AutomaticSilenceDetectionAudioLevel,
    AutomaticSilenceDetectionTime,

    BackgroundEnabled,
    BackgroundWakeWordDetectionTurnOnDisplay,

    WakeWordSoundIndication,
    WakeWordLightIndication,

    Volume,

    ShowLog,
    LogLevel,

    //Configuration Settings
    SiteId,
    BaseSiteId2,

    HttpSSL,

    MQTT_SSL,
    MQTT_ENABLED,
    MQTTHost,
    MQTTPort,
    MQTTUserName,
    MQTTPassword,

    UDPOutput,
    UDPOutputHost,
    UDPOutputPort,

    WakeWordOption,
    WakeWordAccessToken,
    WakeWordKeywordOption,
    WakeWordKeywordSensitivity,

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