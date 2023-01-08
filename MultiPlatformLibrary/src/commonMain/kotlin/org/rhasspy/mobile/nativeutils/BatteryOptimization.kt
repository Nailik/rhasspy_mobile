package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BatteryOptimization {

    /**
     * open battery optimization settings in system
     */
    fun openOptimizationSettings()

    /**
     * check if battery optimization is disabled
     */
    fun isBatteryOptimizationDisabled(): Boolean

}