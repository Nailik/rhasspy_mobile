package org.rhasspy.mobile.viewmodel.screens.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.readOnly

class SettingsScreenViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(SettingsScreenViewState())
    val viewState = _viewState.readOnly

    init {
        viewModelScope.launch {
            combineStateFlow(
                AppSetting.languageType.data,
                AppSetting.isBackgroundServiceEnabled.data,
                AppSetting.microphoneOverlaySizeOption.data,
                AppSetting.isSoundIndicationEnabled.data,
                AppSetting.isWakeWordLightIndicationEnabled.data,
                AppSetting.isAutomaticSilenceDetectionEnabled.data,
                AppSetting.logLevel.data
            ) { arr -> arr }
                .collect { data ->
                    _viewState.update {
                        it.copy(
                            currentLanguage = data[0] as LanguageType,
                            isBackgroundEnabled = data[1] as Boolean,
                            microphoneOverlaySizeOption = data[2] as MicrophoneOverlaySizeOption,
                            isSoundIndicationEnabled = data[3] as Boolean,
                            isWakeWordLightIndicationEnabled = data[4] as Boolean,
                            isAutomaticSilenceDetectionEnabled = data[5] as Boolean,
                            logLevel = data[6] as LogLevel
                        )
                    }
                }
        }
    }

}