package org.rhasspy.mobile.android.settings

/**
 * enum to define all possible settings screens
 */
enum class SettingsScreenType(val route: String) {
    LanguageSettings("SettingsScreenType_LanguageSettings"),
    BackgroundServiceSettings("SettingsScreenType_BackgroundServiceSettings"),
    MicrophoneOverlaySettings("SettingsScreenType_MicrophoneOverlaySettings"),
    IndicationSettings("SettingsScreenType_IndicationSettings"),
    DeviceSettings("SettingsScreenType_DeviceSettings"),
    AudioFocusSettings("SettingsScreenType_AudioFocusSettings"),
    AudioRecorderSettings("SettingsScreenType_AudioRecorderSettings"),
    AutomaticSilenceDetectionSettings("SettingsScreenType_AutomaticSilenceDetectionSettings"),
    LogSettings("SettingsScreenType_LogSettings"),
    SaveAndRestoreSettings("SettingsScreenType_SaveAndRestoreSettings"),
    AboutSettings("SettingsScreenType_AboutSettings")
}