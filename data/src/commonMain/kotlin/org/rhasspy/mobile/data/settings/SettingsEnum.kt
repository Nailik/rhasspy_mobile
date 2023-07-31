package org.rhasspy.mobile.data.settings

enum class SettingsEnum {
    //App Settings
    CrashlyticsDialog,
    ChangelogDialog,
    Crashlytics,
    LanguageOption,
    DialogAutoScroll,

    AutomaticSilenceDetection,
    AutomaticSilenceDetectionAudioLevel,
    AutomaticSilenceDetectionTime,
    AutomaticSilenceDetectionMinimumTime,

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

    MqttApiDeviceChangeEnabled,
    HttpApiDeviceChangeEnabled,
    Volume,
    HotWordEnabled,
    AudioOutputEnabled,
    IntentHandlingEnabled,

    WakeSoundVolume,
    RecordedSoundVolume,
    ErrorSoundVolume,
    WakeSound,
    RecordedSound,
    ErrorSound,
    CustomWakeSounds,
    CustomRecordedSounds,
    CustomErrorSounds,

    AudioFocusOption,
    AudioFocusOnNotification,
    AudioFocusOnSound,
    AudioFocusOnRecord,
    AudioFocusOnDialog,

    AudioRecorderChannel,
    AudioRecorderEncoding,
    AudioRecorderSampleRate,

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

    WakeWordUDPOutputHost,
    WakeWordUDPOutputPort,

    WakeWordOption,
    WakeWordPorcupineAccessToken,
    WakeWordPorcupineAudioRecorderSettings,
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
    AudioPlayingMqttSiteId,

    DialogManagementOption,
    DialogManagementLocalAsrTimeout,
    DialogManagementLocalIntentRecognitionTimeout,
    DialogManagementLocalRecordingTimeout,

    IntentHandlingOption,
    IntentHandlingEndpoint,
    IntentHandlingHassUrl,
    IntentHandlingHassAccessToken,
    IsIntentHandlingHassEvent;

}