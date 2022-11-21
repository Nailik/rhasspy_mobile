package org.rhasspy.mobile.nativeutils

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application

/**
 * handles indication of wake up locally
 */
actual object NativeIndication {

    private val logger = Logger.withTag("NativeIndication")
    private var wakeLock: PowerManager.WakeLock? = null

    /**
     * wake up screen as long as possible
     */
    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    actual fun wakeUpScreen() {
        wakeLock = (Application.Instance.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "Rhasspy::WakeWordDetected"
            ).apply {
                acquire()
            }
        }
    }


    /**
     * remote wake up and let screen go off
     */
    actual fun releaseWakeUp() {
        try {
            wakeLock?.release()
        } catch (e: Exception) {
            logger.w(e) { "wakelock release exception" }
        }
    }

    actual fun checkPermission(): Boolean = OverlayPermission.isGranted()

}