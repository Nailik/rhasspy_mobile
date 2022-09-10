package org.rhasspy.mobile.nativeutils

expect object BatteryOptimization {

    fun openOptimizationSettings()

    fun isBatteryOptimizationDisabled(): Boolean

}