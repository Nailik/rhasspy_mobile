package org.rhasspy.mobile.services.native

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.Application

/**
 * handles indication of wake up locally
 */
actual object NativeIndication {

    val showVisualIndication = MutableLiveData(false)
    var wakeLock: PowerManager.WakeLock? = null

    /**
     * play audio resource
     */
    actual fun playAudio(fileResource: FileResource) {

        val mediaPlayer = MediaPlayer.create(
            Application.Instance,
            fileResource.rawResId
        )

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mediaPlayer.start()
    }

    /**
     * wake up screen as long as possible
     */
    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    actual fun wakeUpScreen() {
        wakeLock =
            (Application.Instance.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
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

        }
    }

    /**
     * acquire permission to draw over other apps
     * by opening intent if necessary
     */
    actual fun displayOverAppsPermission() {
        if (!Settings.canDrawOverlays(Application.Instance)) {
            // send user to the device settings
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Application.Instance.startActivity(myIntent)
        }
    }

    /**
     * display indication over other apps
     */
    actual fun showIndication() {
        showVisualIndication.value = true
    }

    /**
     * close indication over other apps
     */
    actual fun closeIndicationOverOtherApps() {
        showVisualIndication.value = false
    }


}