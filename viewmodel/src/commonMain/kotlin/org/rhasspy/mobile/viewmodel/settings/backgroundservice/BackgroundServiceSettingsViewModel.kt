package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Change.SetBackgroundServiceEnabled

/**
 * background service settings
 *
 * enabled on/off
 * deactivate battery optimization
 */
@Stable
class BackgroundServiceSettingsViewModel(
    viewStateCreator: BackgroundServiceViewStateCreator
) : ViewModel() {

    val viewState: StateFlow<BackgroundServiceViewState> = viewStateCreator()

    fun onEvent(event: BackgroundServiceUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetBackgroundServiceEnabled -> {
                AppSetting.isBackgroundServiceEnabled.value = change.enabled
                if (change.enabled) {
                    BackgroundService.start()
                } else {
                    BackgroundService.stop()
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            DisableBatteryOptimization -> BatteryOptimization.openOptimizationSettings()
        }
    }

}