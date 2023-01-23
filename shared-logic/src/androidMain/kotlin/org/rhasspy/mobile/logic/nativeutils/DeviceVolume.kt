package org.rhasspy.mobile.logic.nativeutils

import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.combineState

/**
 * used to observe device settings for sound or notification volume
 */
actual object DeviceVolume: KoinComponent {

    private val context = get<NativeApplication>()

    private val audioManager = ContextCompat.getSystemService(context, AudioManager::class.java)

    //sound output volume
    private val _volumeFlowSound =
        MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC))
    actual val volumeFlowSound: StateFlow<Int?> get() = _volumeFlowSound

    //notification output volume
    private val _volumeFlowNotification =
        MutableStateFlow(audioManager?.getStreamVolume(AudioManager.STREAM_RING))
    actual val volumeFlowNotification: StateFlow<Int?> get() = _volumeFlowNotification

    private val volumeObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                audioManager?.also {
                    _volumeFlowSound.value = it.getStreamVolume(AudioManager.STREAM_MUSIC)
                    _volumeFlowNotification.value = it.getStreamVolume(AudioManager.STREAM_RING)
                }
                super.onChange(selfChange)
            }
        }

    /**
     * observe content volume when sound of notification flow has subscribers
     */
    init {
        combineState(
            _volumeFlowSound.subscriptionCount,
            _volumeFlowNotification.subscriptionCount
        ) { c1, c2 ->
            c1 + c2 > 0
        }.onEach { isActive -> // configure an action
            if (isActive) {
                context.contentResolver.registerContentObserver(
                    Settings.System.CONTENT_URI,
                    true,
                    volumeObserver
                )
            } else {
                context.contentResolver.unregisterContentObserver(volumeObserver)
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }
}