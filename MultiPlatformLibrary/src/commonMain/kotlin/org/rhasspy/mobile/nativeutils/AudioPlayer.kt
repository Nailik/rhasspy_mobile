package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.AudioOutputOptions

expect class AudioPlayer() : Closeable {

    //TODO implement close
    val isPlayingState: StateFlow<Boolean>

    fun playData(
        data: List<Byte>,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    )

    fun playSoundFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    )

    fun playSoundFile(
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    )

    fun stop()

}