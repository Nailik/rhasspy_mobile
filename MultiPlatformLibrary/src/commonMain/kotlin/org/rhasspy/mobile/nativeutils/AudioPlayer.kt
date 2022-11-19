package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.AudioOutputOptions

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class AudioPlayer() {

    val isPlayingState: StateFlow<Boolean>

    suspend fun playData(data: List<Byte>, onFinished: suspend () -> Unit)

    fun stopPlayingData()

    fun playSoundFileResource(fileResource: FileResource, volume: StateFlow<Float>, audioOutputOptions: AudioOutputOptions)

    fun playSoundFile(subfolder: String, filename: String, volume: StateFlow<Float>, audioOutputOptions: AudioOutputOptions)


}