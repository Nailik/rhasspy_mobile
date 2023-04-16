package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.logic.settings.AppSetting

@Stable
data class SettingsScreenViewState(
    val currentLanguage: LanguageType = AppSetting.languageType.value,
    val isBackgroundEnabled: Boolean = AppSetting.isBackgroundServiceEnabled.value,
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
    val isSoundIndicationEnabled: Boolean = AppSetting.isSoundIndicationEnabled.value,
    val isWakeWordLightIndicationEnabled: Boolean = AppSetting.isWakeWordLightIndicationEnabled.value,
    val isAutomaticSilenceDetectionEnabled: Boolean = AppSetting.isAutomaticSilenceDetectionEnabled.value,
    val logLevel: LogLevel = AppSetting.logLevel.value
)