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

    VoiceActivityDetectionDomain,

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

    ShowLog,
    LogAudioFrames,
    LogLevel,
    LogAutoscroll,

    //Configuration Settings
    SiteId,

    WakeDomain,
    MicDomain,
    AsrDomain,
    HandleDomain,
    IntentDomain,
    TtsDomain,
    SndDomain,

    Pipeline

}