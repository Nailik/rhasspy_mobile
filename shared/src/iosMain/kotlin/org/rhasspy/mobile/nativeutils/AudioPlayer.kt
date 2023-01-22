package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.option.AudioOutputOption

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
    actual fun playData(
        data: List<Byte>,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
    }

    /**
     * play file from resources
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
    }

    /**
     * play file from storage
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playSoundFile(
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
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