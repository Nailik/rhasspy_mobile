package org.rhasspy.mobile.viewmodel.screens.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class SettingsScreenViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<SettingsScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                AppSetting.languageType.data,
                AppSetting.isBackgroundServiceEnabled.data,
                AppSetting.microphoneOverlaySizeOption.data,
                AppSetting.isSoundIndicationEnabled.data,
                AppSetting.isWakeWordLightIndicationEnabled.data,
                AppSetting.isAutomaticSilenceDetectionEnabled.data,
                AppSetting.logLevel.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): SettingsScreenViewState {
        return SettingsScreenViewState(
            currentLanguage = AppSetting.languageType.value,
            isBackgroundEnabled = AppSetting.isBackgroundServiceEnabled.value,
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            isSoundIndicationEnabled = AppSetting.isSoundIndicationEnabled.value,
            isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.value,
            isAutomaticSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.value,
            logLevel = AppSetting.logLevel.value
        )
    }

}