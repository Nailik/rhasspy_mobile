package org.rhasspy.mobile.logic.nativeutils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object BatteryOptimization : KoinComponent {

    private val context = get<NativeApplication>()

    /**
     * open battery optimization settings in system
     */
    @SuppressLint("BatteryLife")
    actual fun openOptimizationSettings() {
        context.startActivity(Intent().apply {
            this.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            this.data = Uri.parse("package:${context.packageName}")
            this.flags = FLAG_ACTIVITY_NEW_TASK
        })
    }

    /**
     * check if battery optimization is disabled
     */
    actual fun isBatteryOptimizationDisabled(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

}