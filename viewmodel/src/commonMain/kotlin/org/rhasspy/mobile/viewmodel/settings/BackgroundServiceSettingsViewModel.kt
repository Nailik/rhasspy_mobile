package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.logic.settings.AppSetting

/**
 * background service settings
 *
 * enabled on/off
 * deactivate battery optimization
 */
class BackgroundServiceSettingsViewModel : ViewModel() {

    //unsaved data
    private var _isBatteryOptimizationDisabled =
        MutableStateFlow(BatteryOptimization.isBatteryOptimizationDisabled())

    //unsaved ui data
    val isBackgroundServiceEnabled = AppSetting.isBackgroundServiceEnabled.data
    val isBatteryOptimizationVisible = isBackgroundServiceEnabled
    val isBatteryOptimizationDisabled = _isBatteryOptimizationDisabled.readOnly

    //set new intent background option
    fun toggleBackgroundServiceEnabled(enabled: Boolean) {
        AppSetting.isBackgroundServiceEnabled.value = enabled
        if (enabled) {
            BackgroundService.start()
        } else {
            BackgroundService.stop()
        }
    }

    //update battery optimization enabled when resume
    fun onResume() {
        _isBatteryOptimizationDisabled.value = BatteryOptimization.isBatteryOptimizationDisabled()
    }

    //open optimization settings to disable battery optimization
    fun onDisableBatteryOptimization() = BatteryOptimization.openOptimizationSettings()

}