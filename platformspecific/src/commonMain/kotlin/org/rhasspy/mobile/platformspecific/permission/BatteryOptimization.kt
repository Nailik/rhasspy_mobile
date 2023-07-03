package org.rhasspy.mobile.platformspecific.permission

import org.rhasspy.mobile.platformspecific.application.INativeApplication

expect class BatteryOptimization(
    nativeApplication: INativeApplication
) {

    /**
     * check if battery optimization is disabled
     */
    fun isBatteryOptimizationDisabled(): Boolean

}