package org.rhasspy.mobile.viewmodel.screens.settings

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class SettingsScreenViewStateCreator {

    operator fun invoke(): StateFlow<SettingsScreenViewState> {

        return ateFlow(
            AppSetting.languageType.data,
            AppSetting.themeType.data,
            AppSetting.isBackgroundServiceEnabled.data,
            AppSetting.microphoneOverlaySizeOption.data,
            AppSetting.isWakeWordLightIndicationEnabled.data,
            AppSetting.audioFocusOption.data,
            AppSetting.logLevel.data,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): SettingsScreenViewState {
        return SettingsScreenViewState(
            currentLanguage = AppSetting.languageType.value,
            currentTheme = AppSetting.themeType.value,
            isBackgroundEnabled = AppSetting.isBackgroundServiceEnabled.value,
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.value,
            audioFocusOption = AppSetting.audioFocusOption.value,
            logLevel = AppSetting.logLevel.value
        )
    }

}