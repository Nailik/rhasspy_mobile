package org.rhasspy.mobile.services.native

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.AppSettings
import java.io.File

/**
 * handles indication of wake up locally
 */
actual object NativeIndication {

    val showVisualIndication = MutableLiveData(false)
    private var wakeLock: PowerManager.WakeLock? = null

    private fun playSound(mediaPlayer: MediaPlayer) {
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        mediaPlayer.setVolume(AppSettings.soundVolume.data, AppSettings.soundVolume.data)

        mediaPlayer.start()
    }

    /**
     * play audio resource
     */
    actual fun playSoundFileResource(fileResource: FileResource) {
        playSound(
            MediaPlayer.create(
                Application.Instance,
                fileResource.rawResId
            )
        )
    }

    /**
     * play some sound file
     */
    actual fun playSoundFile(filename: String) {
        val soundFile = File(File(Application.Instance.filesDir, "sounds").path)

        playSound(
            MediaPlayer.create(
                Application.Instance,
                soundFile.toUri()
            )
        )
    }

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

        }
    }

    /**
     * display indication over other apps
     */
    actual fun showIndication() {
        showVisualIndication.postValue(true)
    }

    /**
     * close indication over other apps
     */
    actual fun closeIndicationOverOtherApps() {
        showVisualIndication.postValue(false)
    }


}