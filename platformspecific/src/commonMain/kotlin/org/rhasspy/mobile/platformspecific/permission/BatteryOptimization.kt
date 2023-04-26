package org.rhasspy.mobile.platformspecific.permission

expect object BatteryOptimization {

    /**
     * check if battery optimization is disabled
     */
    fun isBatteryOptimizationDisabled(): Boolean

}