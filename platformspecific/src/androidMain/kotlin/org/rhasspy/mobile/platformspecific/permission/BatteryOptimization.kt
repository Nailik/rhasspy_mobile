package org.rhasspy.mobile.platformspecific.permission

import android.content.Context
import android.os.PowerManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object BatteryOptimization : KoinComponent {

    private val context by inject<NativeApplication>()

    /**
     * check if battery optimization is disabled
     */
    actual fun isBatteryOptimizationDisabled(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

}