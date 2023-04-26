package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalRedirect
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectIntention
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.*
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Change.SetBackgroundServiceEnabled
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Consumed.ShowSnackBar

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

    private val _viewState: MutableStateFlow<BackgroundServiceViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: BackgroundServiceUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
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
            DisableBatteryOptimization -> disableBatteryOptimization()
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun disableBatteryOptimization() {
        viewModelScope.launch(Dispatchers.Default) {
            ExternalRedirect.launch(ExternalRedirectIntention.OpenBatteryOptimizationSettings)
            _viewState.update {
                it.copy(snackBarText = MR.strings.disableBatteryOptimizationFailed.stable)
            }
        }
    }

}