package org.rhasspy.mobile.nativeutils

import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.Application

actual object DeviceVolume {


    private val audioManager = ContextCompat.getSystemService(Application.Instance, AudioManager::class.java)
    private val _volumeFlowSound = MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC))
    private val _volumeFlowNotification = MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_RING))

    actual val volumeFlowSound: StateFlow<Int?>
        get() = _volumeFlowSound
    actual val volumeFlowNotification: StateFlow<Int?>
        get() = _volumeFlowNotification

    private val mVolumeObserver: ContentObserver = object : ContentObserver(Looper.myLooper()?.let { Handler(it) }) {
        override fun onChange(selfChange: Boolean) {
            audioManager?.also {
                _volumeFlowSound.value = it.getStreamVolume(AudioManager.STREAM_MUSIC)
                _volumeFlowNotification.value = it.getStreamVolume(AudioManager.STREAM_RING)
            }
            super.onChange(selfChange)
        }
    }

    init {
        Application.Instance.contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, mVolumeObserver)
    }

}