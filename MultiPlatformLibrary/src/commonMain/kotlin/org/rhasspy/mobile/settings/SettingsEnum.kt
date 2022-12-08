package org.rhasspy.mobile.settings

enum class SettingsEnum {
    //App Settings
    Crashlytics,
    LanguageOption,
    ThemeOption,

    AutomaticSilenceDetection,
    AutomaticSilenceDetectionAudioLevel,
    AutomaticSilenceDetectionTime,

    BackgroundEnabled,
    SSLVerificationDisabled,
    HttpServerEndpointHost,
    HttpServerEndpointPort,

    MicrophoneOverlaySize,
    MicrophoneOverlayWhileApp,
    MicrophoneOverlayPositionX,
    MicrophoneOverlayPositionY,

    BackgroundWakeWordDetectionTurnOnDisplay,
    SoundIndication,
    WakeWordLightIndication,
    SoundIndicationOutput,

    HotWordEnabled,
    AudioOutputEnabled,
    IntentHandlingEnabled,
    Volume,

    WakeSoundVolume,
    RecordedSoundVolume,
    ErrorSoundVolume,
    WakeSound,
    RecordedSound,
    ErrorSound,
    CustomWakeSounds,
    CustomRecordedSounds,
    CustomErrorSounds,


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
    SpeechToTextMqttSilenceDetection,

    IntentRecognitionOption,
    CustomIntentRecognitionHttpEndpoint,
    IntentRecognitionHttpEndpoint,

    TextToSpeechOption,
    CustomTextToSpeechOptionHttpEndpoint,
    TextToSpeechHttpEndpoint,

    AudioPlayingOption,
    AudioOutputOption,
    CustomAudioPlayingHttpEndpoint,
    AudioPlayingHttpEndpoint,

    DialogManagementOption,

    IntentHandlingOption,
    IntentHandlingEndpoint,
    IntentHandlingHassUrl,
    IntentHandlingHassAccessToken,
    IsIntentHandlingHassEvent
}