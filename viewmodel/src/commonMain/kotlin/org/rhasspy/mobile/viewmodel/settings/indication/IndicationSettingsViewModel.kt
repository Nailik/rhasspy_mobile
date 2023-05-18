package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.settings.IndicationSettingsScreenDestination
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.*

@Stable
class IndicationSettingsViewModel() : KViewModel() {

    private val _viewState = MutableStateFlow(IndicationSettingsViewState())
    val viewState = _viewState.readOnly

    val screen = navigator.topScreen<IndicationSettingsScreenDestination>()

    fun onEvent(event: IndicationSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SetSoundIndicationEnabled -> {
                    AppSetting.isSoundIndicationEnabled.value = change.enabled
                    it.copy(isSoundIndicationEnabled = change.enabled)
                }

                is SelectSoundIndicationOutputOption -> {
                    AppSetting.soundIndicationOutputOption.value = change.option
                    it.copy(soundIndicationOutputOption = change.option)
                }

                is SetWakeWordDetectionTurnOnDisplay -> {
                    AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = change.enabled
                    it.copy(isWakeWordDetectionTurnOnDisplayEnabled = change.enabled)
                }

                is SetWakeWordLightIndicationEnabled -> {
                    AppSetting.isWakeWordLightIndicationEnabled.value = change.enabled
                    it.copy(isWakeWordLightIndicationEnabled = change.enabled)
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}