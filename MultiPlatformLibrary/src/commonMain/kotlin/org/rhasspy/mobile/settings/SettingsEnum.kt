package org.rhasspy.mobile.settings

enum class SettingsEnum {
    //App Settings
    Crashlytics,
    LanguageOption,

    AutomaticSilenceDetection,
    AutomaticSilenceDetectionAudioLevel,
    AutomaticSilenceDetectionTime,

    BackgroundEnabled,
    SSLVerificationDisabled,
    HttpClientServerEndpointHost,
    HttpClientServerEndpointPort,
    HttpClientTimeout,

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
    LogAutoscroll,

    //Configuration Settings
    SiteId,

    HttpServerEnabled,
    HttpServerPort,
    HttpServerSSLEnabled,
    HttpServerSSLKeyStoreFile,
    HttpServerSSLKeyStorePassword,
    HttpServerSSLKeyAlias,
    HttpServerSSLKeyPassword,

    MQTTEnabled,
    MQTTHost,
    MQTTPort,
    MQTTUserName,
    MQTTSSLEnabled,
    MQTTPassword,
    MQTTConnectionTimeout,
    MQTTKeepAliveInterval,
    MQTTRetryInterval,
    MQTTKeyStoreFile,

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
    IsIntentHandlingHassEvent;

}