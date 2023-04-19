package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateViewStateFlow
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.*

@Stable
class IndicationSettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(IndicationSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: IndicationSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.updateViewStateFlow {
            when (change) {
                is SetSoundIndicationEnabled -> {
                    AppSetting.isSoundIndicationEnabled.value = change.enabled
                    copy(isSoundIndicationEnabled = change.enabled)
                }

                is SetSoundIndicationOutputOption -> {
                    AppSetting.soundIndicationOutputOption.value = change.option
                    copy(soundIndicationOutputOption = change.option)
                }

                is SetWakeWordDetectionTurnOnDisplay -> {
                    AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = change.enabled
                    copy(isWakeWordDetectionTurnOnDisplayEnabled = change.enabled)
                }

                is SetWakeWordLightIndicationEnabled -> {
                    AppSetting.isWakeWordLightIndicationEnabled.value = change.enabled
                    copy(isWakeWordLightIndicationEnabled = change.enabled)
                }
            }
        }
    }

}