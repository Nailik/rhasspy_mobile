package org.rhasspy.mobile.viewmodel.settings.indication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class IndicationSettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<IndicationSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())
        //live update when settings change from mqtt/ webserver
        updaterScope.launch {
            combineStateFlow(
                AppSetting.isSoundIndicationEnabled.data,
                AppSetting.isWakeWordLightIndicationEnabled.data,
                AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.data,
                AppSetting.soundIndicationOutputOption.data,
                AppSetting.wakeSound.data,
                AppSetting.recordedSound.data,
                AppSetting.errorSound.data
            ).collect { viewState.value = getViewState() }
        }
        return viewState
    }

    private fun getViewState(): IndicationSettingsViewState {
        return IndicationSettingsViewState(
            isSoundIndicationEnabled = AppSetting.isSoundIndicationEnabled.value,
            isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.value,
            isWakeWordDetectionTurnOnDisplayEnabled = AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value,
            soundIndicationOutputOption = AppSetting.soundIndicationOutputOption.value,
            wakeSound = AppSetting.wakeSound.value,
            recordedSound = AppSetting.recordedSound.value,
            errorSound = AppSetting.errorSound.value
        )
    }

}