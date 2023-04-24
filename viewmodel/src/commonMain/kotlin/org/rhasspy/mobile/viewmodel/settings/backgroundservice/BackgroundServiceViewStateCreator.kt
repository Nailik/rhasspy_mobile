package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.settings.AppSetting

class BackgroundServiceViewStateCreator(
    private val nativeApplication: NativeApplication
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<BackgroundServiceViewState> {
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

    private fun getViewState(): BackgroundServiceViewState {
        return BackgroundServiceViewState(
            isBackgroundServiceEnabled = AppSetting.isBackgroundServiceEnabled.value,
            isBatteryOptimizationDisabled = BatteryOptimization.isBatteryOptimizationDisabled()
        )
    }

}