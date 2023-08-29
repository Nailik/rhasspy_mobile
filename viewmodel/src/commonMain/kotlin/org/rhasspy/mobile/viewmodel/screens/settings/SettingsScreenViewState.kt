package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.theme.ThemeType

@Stable
data class SettingsScreenViewState internal constructor(
    val currentLanguage: LanguageType,
    val currentTheme: ThemeType,
    val isBackgroundEnabled: Boolean,
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
    val isSoundIndicationEnabled: Boolean,
    val isWakeWordLightIndicationEnabled: Boolean,
    val audioFocusOption: AudioFocusOption,
    val logLevel: LogLevel
)