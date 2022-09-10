package org.rhasspy.mobile.nativeutils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import org.rhasspy.mobile.Application

actual object BatteryOptimization {

    actual fun openOptimizationSettings() {
        Application.Instance.startActivity(Intent().apply {
            this.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            this.data = Uri.parse("package:${Application.Instance.packageName}")
            this.flags = FLAG_ACTIVITY_NEW_TASK
        })
    }

    actual fun isBatteryOptimizationDisabled(): Boolean {
        val powerManager = Application.Instance.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(Application.Instance.packageName)
    }

}