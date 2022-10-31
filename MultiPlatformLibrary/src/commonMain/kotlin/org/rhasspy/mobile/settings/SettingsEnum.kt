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
    HttpServerEndpoint,

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
    CustomSounds,

    ShowLog,
    LogAudioFrames,
    LogLevel,

    ForceCancel,

    //Configuration Settings
    SiteId,

    HttpServerEnabled,
    HttpServerPort,
    HttpServerSSL,

    MQTT_ENABLED,
    MQTTHost,
    MQTTPort,
    MQTTUserName,
    MQTT_SSL,
    MQTTPassword,
    MQTTConnectionTimeout,
    MQTTKeepAliveInterval,
    MQTTRetryInterval,

    UDPOutput,
    UDPOutputHost,
    UDPOutputPort,

    WakeWordOption,
    WakeWordPorcupineAccessToken,
    WakeWordPorcupineKeywordDefaultSelectedOptions,
    WakeWordPorcupineKeywordCustomOptions,
    WakeWordPorcupineLanguage,

    SpeechToTextOption,
    CustomSpeechToTextEndpoint,
    SpeechToTextHttpEndpoint,

    IntentRecognitionOption,
    CustomIntentRecognitionHttpEndpoint,
    IntentRecognitionHttpEndpoint,

    TextToSpeechOption,
    CustomTextToSpeechOptionHttpEndpoint,
    TextToSpeechHttpEndpoint,

    AudioPlayingOption,
    CustomAudioPlayingHttpEndpoint,
    AudioPlayingHttpEndpoint,

    DialogManagementOption,

    IntentHandlingOption,
    IntentHandlingEndpoint,
    IntentHandlingHassUrl,
    IntentHandlingHassAccessToken,
    IsIntentHandlingHassEvent
}