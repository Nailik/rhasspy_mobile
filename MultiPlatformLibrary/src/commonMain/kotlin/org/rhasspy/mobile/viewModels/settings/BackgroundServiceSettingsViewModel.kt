package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.nativeutils.BatteryOptimization
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class BackgroundServiceSettingsViewModel : ViewModel() {

    //unsaved data
    private val _isBackgroundServiceEnabled = MutableStateFlow(AppSettings.isBackgroundServiceEnabled.value)

    //unsaved ui data
    val isBackgroundServiceEnabled = _isBackgroundServiceEnabled.readOnly
    private var _isBatteryOptimizationDisabled = MutableStateFlow(BatteryOptimization.isBatteryOptimizationDisabled())
    val isBatteryOptimizationDisabled = _isBatteryOptimizationDisabled.readOnly

    //set new intent recognition option
    fun toggleBackgroundServiceEnabled(enabled: Boolean) {
        _isBackgroundServiceEnabled.value = enabled
    }

    /**
     * update battery optimization enabled when resume
     */
    fun onResume() {
        _isBatteryOptimizationDisabled.value = BatteryOptimization.isBatteryOptimizationDisabled()
    }

    /**
     * open optimization settings to disable battery optimization
     */
    fun onDisableBatteryOptimization() = BatteryOptimization.openOptimizationSettings()

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isBackgroundServiceEnabled.value = _isBackgroundServiceEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}