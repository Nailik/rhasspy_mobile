package org.rhasspy.mobile.services.native

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.services.Application

actual object NativeIndication {

    val showVisualIndication = MutableLiveData(false)

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

    var wakeLock: PowerManager.WakeLock? = null

    @Suppress("DEPRECATION")
    actual fun wakeUpScreen() {
        wakeLock =
            (Application.Instance.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "MyApp::MyWakelockTag"
                ).apply {
                    acquire(20L)
                }
            }
        //release on silence detection
    }

    actual fun releaseWakeUp(){
        wakeLock?.release()
    }

    actual fun displayOverAppsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(Application.Instance)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                Application.Instance.startActivity(myIntent)
            }
        }
    }

    actual fun showDisplayOverOtherApps() {
        // and display the content on screen
        // and display the content on screen
        showVisualIndication.value = true
    }

    actual fun closeDisplayOverOtherApps() {
        showVisualIndication.value = false
    }


}