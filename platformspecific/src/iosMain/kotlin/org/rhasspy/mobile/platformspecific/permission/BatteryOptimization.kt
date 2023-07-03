package org.rhasspy.mobile.platformspecific.permission

import org.rhasspy.mobile.platformspecific.application.INativeApplication

actual class BatteryOptimization actual constructor(
    private val nativeApplication: INativeApplication
) {

    actual fun isBatteryOptimizationDisabled(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}