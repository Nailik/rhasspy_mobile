package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.settings.AppSetting

class BackgroundServiceSettingsViewStateCreator(
    private val nativeApplication: NativeApplication,
    private val batteryOptimization: BatteryOptimization
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): MutableStateFlow<BackgroundServiceSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                nativeApplication.isAppInBackground,
                AppSetting.isBackgroundServiceEnabled.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): BackgroundServiceSettingsViewState {
        return BackgroundServiceSettingsViewState(
            isBackgroundServiceEnabled = AppSetting.isBackgroundServiceEnabled.value,
            isBatteryOptimizationDisabled = batteryOptimization.isBatteryOptimizationDisabled()
        )
    }

}