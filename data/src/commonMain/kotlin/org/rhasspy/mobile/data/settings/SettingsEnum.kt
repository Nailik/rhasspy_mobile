package org.rhasspy.mobile.data.settings

enum class SettingsEnum {
    //App Settings
    CrashlyticsDialog,
    ChangelogDialog,
    Crashlytics,
    LanguageOption,
    ThemeOption,
    DialogAutoScroll,

    VoiceActivityDetectionOption,
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
    AudioRecorderPauseRecordingOnMedia,

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

    WakeWordAudioRecorderChannel,
    WakeWordAudioRecorderEncoding,
    WakeWordAudioRecorderSampleRate,

    WakeWordAudioOutputChannel,
    WakeWordAudioROutputEncoding,
    WakeWordAudioOutputSampleRate,

    WakeWordPorcupineAccessToken,
    WakeWordPorcupineKeywordDefaultSelectedOptions,
    WakeWordPorcupineKeywordCustomOptions,
    WakeWordPorcupineLanguage,

    SpeechToTextOption,
    SpeechToTextAudioRecorderChannel,
    SpeechToTextAudioRecorderEncoding,
    SpeechToTextAudioRecorderSampleRate,
    SpeechToTextAudioOutputChannel,
    SpeechToTextAudioOutputEncoding,
    SpeechToTextAudioOutputSampleRate,
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