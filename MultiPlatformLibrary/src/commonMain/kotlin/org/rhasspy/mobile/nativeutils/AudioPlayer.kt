package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.AudioOutputOptions

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
    fun playData(
        data: List<Byte>,
        volume: Float,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * play file from resources
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * play file from storage
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playSoundFile(
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * stop playback
     */
    fun stop()

}