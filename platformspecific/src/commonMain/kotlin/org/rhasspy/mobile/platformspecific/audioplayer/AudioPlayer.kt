package org.rhasspy.mobile.platformspecific.audioplayer

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.AudioOutputOption

/**
 * plays audio on the device
 *
 * some data (Byte list)
 * File resource
 * specific file
 */
expect class AudioPlayer() : Closeable {

    /**
     * represents if audio player is currently playing
     */
    val isPlayingState: StateFlow<Boolean>

    /**
     * play byte list
     *
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: () -> Unit
    )

    /**
     * stop playback
     */
    fun stop()

}