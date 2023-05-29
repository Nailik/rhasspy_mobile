package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Success
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.*
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Consumed.ShowSnackBar

/**
 * background service settings
 *
 * enabled on/off
 * deactivate battery optimization
 */
@Stable
class BackgroundServiceSettingsViewModel(
    viewStateCreator: BackgroundServiceSettingsViewStateCreator,
    private val backgroundService: BackgroundService
) : KViewModel() {

    private val _viewState: MutableStateFlow<BackgroundServiceSettingsViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: BackgroundServiceSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetBackgroundServiceSettingsEnabled -> {
                AppSetting.isBackgroundServiceEnabled.value = change.enabled
                if (change.enabled) {
                    backgroundService.start()
                } else {
                    backgroundService.stop()
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            DisableBatteryOptimization -> disableBatteryOptimization()
            is BackClick -> navigator.onBackPressed()
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
        viewModelScope.launch(Dispatchers.IO) {
            if (externalResultRequest.launch(ExternalResultRequestIntention.OpenBatteryOptimizationSettings) !is Success) {
                _viewState.update {
                    it.copy(snackBarText = MR.strings.disableBatteryOptimizationFailed.stable)
                }
            }
        }
    }

}