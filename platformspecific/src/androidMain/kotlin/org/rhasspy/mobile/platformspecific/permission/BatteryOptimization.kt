package org.rhasspy.mobile.platformspecific.permission

import android.os.PowerManager
import androidx.core.content.getSystemService
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class BatteryOptimization actual constructor(
    private val nativeApplication: INativeApplication
) {

    private val logger = Logger.withTag("BatteryOptimization")

    /**
     * check if battery optimization is disabled
     */
    actual fun isBatteryOptimizationDisabled(): Boolean {
        return (nativeApplication as NativeApplication).getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(nativeApplication.packageName)
            ?: run {
                logger.e { "isBatteryOptimizationDisabled powerManager is null" }
                false
            }
    }

}