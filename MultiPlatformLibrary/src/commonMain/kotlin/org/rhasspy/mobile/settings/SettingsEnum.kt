package org.rhasspy.mobile.settings

enum class SettingsEnum {
    //App Settings
    LanguageOption,
    ThemeOption,

    AutomaticSilenceDetection,
    AutomaticSilenceDetectionAudioLevel,
    AutomaticSilenceDetectionTime,

    BackgroundEnabled,
    SSLVerificationDisabled,

    MicrophoneOverlay,
    MicrophoneOverlayWhileApp,
    MicrophoneOverlayPositionX,
    MicrophoneOverlayPositionY,

    BackgroundWakeWordDetectionTurnOnDisplay,
    WakeWordSoundIndication,
    WakeWordLightIndication,

    HotWordEnabled,
    AudioOutputEnabled,
    IntentHandlingEnabled,
    Volume,

    SoundVolume,
    WakeSound,
    RecordedSound,
    ErrorSound,
    WakeSounds,
    RecordedSounds,
    ErrorSounds,
    CustomSounds,

    ShowLog,
    LogAudioFrames,
    LogLevel,

    //Configuration Settings
    SiteId,

    HttpServerEnabled,
    HttpServerPort,
    HttpServerSSL,

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
    WakeWordPorcupineAccessToken,
    WakeWordPorcupineKeywordOption,
    WakeWordPorcupineKeywordOptions,
    WakeWordPorcupineLanguage,
    WakeWordPorcupineKeywordSensitivity,

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