package org.rhasspy.mobile.nativeutils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import org.rhasspy.mobile.Application

actual object BatteryOptimization {

    /**
     * open battery optimization settings in system
     */
    @SuppressLint("BatteryLife")
    actual fun openOptimizationSettings() {
        Application.nativeInstance.startActivity(Intent().apply {
            this.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            this.data = Uri.parse("package:${Application.nativeInstance.packageName}")
            this.flags = FLAG_ACTIVITY_NEW_TASK
        })
    }

    /**
     * check if battery optimization is disabled
     */
    actual fun isBatteryOptimizationDisabled(): Boolean {
        val powerManager =
            Application.nativeInstance.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(Application.nativeInstance.packageName)
    }

}