package org.rhasspy.mobile.platformspecific.permission

import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect class BatteryOptimization(
    nativeApplication: NativeApplication,
) {

    /**
     * check if battery optimization is disabled
     */
    fun isBatteryOptimizationDisabled(): Boolean

}