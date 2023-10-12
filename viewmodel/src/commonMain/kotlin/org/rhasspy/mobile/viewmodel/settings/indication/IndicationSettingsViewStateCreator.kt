package org.rhasspy.mobile.viewmodel.settings.indication

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class IndicationSettingsViewStateCreator {

    operator fun invoke(): StateFlow<IndicationSettingsViewState> {

        return combineStateFlow(
            AppSetting.isWakeWordLightIndicationEnabled.data,
            AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.data,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): IndicationSettingsViewState {
        return IndicationSettingsViewState(
            isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.value,
            isWakeWordDetectionTurnOnDisplayEnabled = AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value,
        )
    }

}