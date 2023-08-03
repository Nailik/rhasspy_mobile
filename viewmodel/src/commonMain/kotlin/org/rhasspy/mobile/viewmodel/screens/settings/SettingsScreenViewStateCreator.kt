package org.rhasspy.mobile.viewmodel.screens.settings

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class SettingsScreenViewStateCreator {

    operator fun invoke(): StateFlow<SettingsScreenViewState> {

        return combineStateFlow(
            AppSetting.languageType.data,
            AppSetting.isBackgroundServiceEnabled.data,
            AppSetting.microphoneOverlaySizeOption.data,
            AppSetting.isSoundIndicationEnabled.data,
            AppSetting.isWakeWordLightIndicationEnabled.data,
            AppSetting.audioFocusOption.data,
            AppSetting.isAutomaticSilenceDetectionEnabled.data,
            AppSetting.logLevel.data,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): SettingsScreenViewState {
        return SettingsScreenViewState(
            currentLanguage = AppSetting.languageType.value,
            isBackgroundEnabled = AppSetting.isBackgroundServiceEnabled.value,
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            isSoundIndicationEnabled = AppSetting.isSoundIndicationEnabled.value,
            isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.value,
            audioFocusOption = AppSetting.audioFocusOption.value,
            isAutomaticSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.value,
            logLevel = AppSetting.logLevel.value
        )
    }

}