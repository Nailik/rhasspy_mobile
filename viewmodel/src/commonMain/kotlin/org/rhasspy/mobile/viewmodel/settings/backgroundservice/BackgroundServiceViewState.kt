package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization

data class BackgroundServiceViewState(
    val isBackgroundServiceEnabled: Boolean = AppSetting.isBackgroundServiceEnabled.value,
    val isBatteryOptimizationDisabled: Boolean = BatteryOptimization.isBatteryOptimizationDisabled()
)