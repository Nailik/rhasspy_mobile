package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.settings.IndicationSettingsScreenDestination
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.*

@Stable
class IndicationSettingsViewModel(
    viewStateCreator: IndicationSettingsViewStateCreator
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    val screen = navigator.topScreen<IndicationSettingsScreenDestination>()

    fun onEvent(event: IndicationSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetSoundIndicationEnabled -> AppSetting.isSoundIndicationEnabled.value = change.enabled
            is SelectSoundIndicationOutputOption -> AppSetting.soundIndicationOutputOption.value = change.option
            is SetWakeWordDetectionTurnOnDisplay -> AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = change.enabled
            is SetWakeWordLightIndicationEnabled -> {
                requireOverlayPermission {
                    AppSetting.isWakeWordLightIndicationEnabled.value = change.enabled
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