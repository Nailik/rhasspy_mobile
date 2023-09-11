package org.rhasspy.mobile.data.settings

enum class SettingsEnum {
    Version,
    Rhasspy2Connection,
    Rhasspy3Connection,
    HomeAssistantConnection,
    MqttConnection,
    LocalWebserverConnection,

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

    SpeechToTextMqttSilenceDetection,

    IntentRecognitionOption,

    TextToSpeechOption,

    AudioPlayingOption,
    AudioOutputOption,

    AudioPlayingMqttSiteId,

    DialogManagementOption,
    DialogManagementLocalAsrTimeout,
    DialogManagementLocalIntentRecognitionTimeout,
    DialogManagementLocalRecordingTimeout,

    IntentHandlingOption,
    IsIntentHandlingHassEvent;

}