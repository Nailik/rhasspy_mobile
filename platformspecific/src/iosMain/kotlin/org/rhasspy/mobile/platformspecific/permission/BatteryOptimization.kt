package org.rhasspy.mobile.platformspecific.permission

import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class BatteryOptimization actual constructor(
    private val nativeApplication: NativeApplication
) {

    actual fun isBatteryOptimizationDisabled(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}