package org.rhasspy.mobile.platformspecific.audioplayer

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption

actual class AudioPlayer : Closeable {
    /**
     * represents if audio player is currently playing
     */
    actual val isPlayingState: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    /**
     * play byte list
     *
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (exception: Exception?) -> Unit
    ) {
    }

    /**
     * stop playback
     */
    actual fun stop() {
    }

    override fun close() {
        TODO("Not yet implemented")
    }


}