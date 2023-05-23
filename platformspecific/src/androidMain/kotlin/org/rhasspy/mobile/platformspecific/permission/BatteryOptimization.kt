package org.rhasspy.mobile.platformspecific.permission

import android.os.PowerManager
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class BatteryOptimization actual constructor(
    private val nativeApplication: NativeApplication
) {

    /**
     * check if battery optimization is disabled
     */
    actual fun isBatteryOptimizationDisabled(): Boolean {
        val powerManager: PowerManager? = nativeApplication.getSystemService(PowerManager::class.java)
        return powerManager?.isIgnoringBatteryOptimizations(nativeApplication.packageName) ?: false
    }

}