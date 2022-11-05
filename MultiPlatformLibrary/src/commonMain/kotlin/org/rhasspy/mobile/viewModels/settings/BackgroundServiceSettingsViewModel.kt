package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.nativeutils.BatteryOptimization
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

/**
 * background service settings
 *
 * enabled on/off
 * deactivate battery optimization
 */
class BackgroundServiceSettingsViewModel : ViewModel() {

    //unsaved data
    private var _isBatteryOptimizationDisabled = MutableStateFlow(BatteryOptimization.isBatteryOptimizationDisabled())

    //unsaved ui data
    val isBackgroundServiceEnabled = AppSettings.isBackgroundServiceEnabled.data
    val isBatteryOptimizationVisible = isBackgroundServiceEnabled
    val isBatteryOptimizationDisabled = _isBatteryOptimizationDisabled.readOnly

    //set new intent background option
    fun toggleBackgroundServiceEnabled(enabled: Boolean) {
        AppSettings.isBackgroundServiceEnabled.value = enabled
    }

    //update battery optimization enabled when resume
    fun onResume() {
        _isBatteryOptimizationDisabled.value = BatteryOptimization.isBatteryOptimizationDisabled()
    }

    //open optimization settings to disable battery optimization
    fun onDisableBatteryOptimization() = BatteryOptimization.openOptimizationSettings()

}