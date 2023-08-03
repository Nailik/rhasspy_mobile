package org.rhasspy.mobile.viewmodel.settings.indication

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class IndicationSettingsViewStateCreator {

    operator fun invoke(): StateFlow<IndicationSettingsViewState> {

        return combineStateFlow(
            AppSetting.isSoundIndicationEnabled.data,
            AppSetting.isWakeWordLightIndicationEnabled.data,
            AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.data,
            AppSetting.soundIndicationOutputOption.data,
            AppSetting.wakeSound.data,
            AppSetting.recordedSound.data,
            AppSetting.errorSound.data
        ).mapReadonlyState {
            getViewState()
        }

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