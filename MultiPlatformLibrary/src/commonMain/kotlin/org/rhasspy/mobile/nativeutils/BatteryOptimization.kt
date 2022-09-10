package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BatteryOptimization {

    fun openOptimizationSettings()

    fun isBatteryOptimizationDisabled(): Boolean

}