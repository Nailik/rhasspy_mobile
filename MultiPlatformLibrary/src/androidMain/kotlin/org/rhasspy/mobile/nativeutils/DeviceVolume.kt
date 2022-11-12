package org.rhasspy.mobile.nativeutils

import android.media.AudioManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application

actual object DeviceVolume {


    private val audioManager = ContextCompat.getSystemService(Application.Instance, AudioManager::class.java)

    private val _volumeFlowSound = MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC))
    private val _volumeFlowNotification = MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_RING))

    actual val volumeFlowSound: StateFlow<Int?>
        get() = _volumeFlowSound
    actual val volumeFlowNotification: StateFlow<Int?>
        get() = _volumeFlowNotification

    private var observerVolumeSound: Job? = null
    private var observerVolumeNotification: Job? = null

    private fun startObserveVolumeSound() {
        observerVolumeSound = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                audioManager?.also {
                    _volumeFlowSound.value =  it.getStreamVolume(AudioManager.STREAM_MUSIC)
                }
                awaitFrame()
            }
        }.apply {
            start()
        }
    }

    private fun startObserveVolumeNotification() {
        observerVolumeNotification = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                audioManager?.also {
                    _volumeFlowNotification.value = it.getStreamVolume(AudioManager.STREAM_RING)
                }
                awaitFrame()
            }
        }.apply {
            start()
        }
    }

    init {
        _volumeFlowSound.subscriptionCount
            .map { count -> count > 0 } // map count into active/inactive flag
            .distinctUntilChanged() // only react to true<->false changes
            .onEach { isActive -> // configure an action
                if (isActive) {
                    startObserveVolumeSound()
                } else {
                    observerVolumeSound?.cancel()
                    observerVolumeSound = null
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))


        _volumeFlowNotification.subscriptionCount
            .map { count -> count > 0 } // map count into active/inactive flag
            .distinctUntilChanged() // only react to true<->false changes
            .onEach { isActive -> // configure an action
                if (isActive) {
                    startObserveVolumeNotification()
                } else {
                    observerVolumeNotification?.cancel()
                    observerVolumeNotification = null
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

}