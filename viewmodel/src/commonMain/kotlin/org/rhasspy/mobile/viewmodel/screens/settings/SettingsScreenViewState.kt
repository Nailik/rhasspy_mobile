package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption

@Stable
data class SettingsScreenViewState internal constructor(
    val currentLanguage: LanguageType,
    val isBackgroundEnabled: Boolean,
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
    val isSoundIndicationEnabled: Boolean,
    val isWakeWordLightIndicationEnabled: Boolean,
    val audioFocusOption: AudioFocusOption,
    val isAutomaticSilenceDetectionEnabled: Boolean,
    val logLevel: LogLevel
)