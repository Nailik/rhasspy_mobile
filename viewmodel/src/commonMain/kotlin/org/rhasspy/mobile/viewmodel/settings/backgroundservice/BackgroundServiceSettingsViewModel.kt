package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.platformspecific.readOnly
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
class BackgroundServiceSettingsViewModel(
    private val nativeApplication: NativeApplication
) : ViewModel() {

    private val _viewState = MutableStateFlow(BackgroundServiceViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: BackgroundServiceUiEvent) {
        when(event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SetBackgroundServiceEnabled -> {
                    AppSetting.isBackgroundServiceEnabled.value = change.enabled
                    if (change.enabled) {
                        BackgroundService.start()
                    } else {
                        BackgroundService.stop()
                    }
                    it.copy(isBackgroundServiceEnabled = change.enabled)
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            DisableBatteryOptimization -> BatteryOptimization.openOptimizationSettings()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if(!isAppInBackground) {
                    _viewState.update {
                        it.copy(isBatteryOptimizationDisabled = BatteryOptimization.isBatteryOptimizationDisabled())
                    }
                }
            }
        }
    }

}