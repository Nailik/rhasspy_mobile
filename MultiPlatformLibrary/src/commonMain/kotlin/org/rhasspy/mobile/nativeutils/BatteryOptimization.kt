package org.rhasspy.mobile.nativeutils

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