package org.rhasspy.mobile.platformspecific.audiorecorder

import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.AudioRecordingConfiguration
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class AudioManagerCallback(
    val callback: (isInUse: Boolean) -> Unit,
    private val audioManager: AudioManager?,
) : IInternalAudioManagerCallback {

    private var audioSessionId: Int? = null

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

    private val audioRecorderCallback = object : AudioManager.AudioRecordingCallback() {
        override fun onRecordingConfigChanged(configs: MutableList<AudioRecordingConfiguration>?) {
            super.onRecordingConfigChanged(configs)
            if (configs?.any { it.clientAudioSessionId != audioSessionId } == true) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    override fun register(audioSessionId: Int?) {
        this.audioSessionId = audioSessionId
        audioManager?.registerAudioPlaybackCallback(audioPlaybackCallback, null)
        audioManager?.registerAudioRecordingCallback(audioRecorderCallback, null)
    }

    override fun unregister() {
        audioManager?.unregisterAudioPlaybackCallback(audioPlaybackCallback)
        audioManager?.unregisterAudioRecordingCallback(audioRecorderCallback)
    }

}