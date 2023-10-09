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
    WakeWordLightIndication,

    MqttApiDeviceChangeEnabled,
    HttpApiDeviceChangeEnabled,
    Volume,
    HotWordEnabled,
    AudioOutputEnabled,
    IntentHandlingEnabled,

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