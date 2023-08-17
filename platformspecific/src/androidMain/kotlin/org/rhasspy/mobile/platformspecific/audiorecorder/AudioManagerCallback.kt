package org.rhasspy.mobile.platformspecific.audiorecorder

import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class AudioManagerCallback(
    val callback: (isPlaying: Boolean) -> Unit,
    private val audioManager: AudioManager?,
) : IInternalAudioManagerCallback {

    private val audioPlaybackCallback = object : AudioManager.AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: MutableList<AudioPlaybackConfiguration>?) {
            super.onPlaybackConfigChanged(configs)

            if (configs?.isNotEmpty() == true) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    override fun register() {
        audioManager?.registerAudioPlaybackCallback(audioPlaybackCallback, null)
    }

    override fun unregister() {
        audioManager?.unregisterAudioPlaybackCallback(audioPlaybackCallback)
    }

}