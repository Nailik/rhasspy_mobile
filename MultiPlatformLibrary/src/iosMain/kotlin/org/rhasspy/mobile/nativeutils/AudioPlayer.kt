package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import kotlinx.coroutines.flow.StateFlow

actual object AudioPlayer {

    actual fun playData(data: List<Byte>, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    actual val isPlayingState: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    actual fun playSoundFileResource(fileResource: FileResource, volume: Float) {
        TODO("Not yet implemented")
    }

    actual fun playSoundFile(subfolder: String, filename: String, volume: Float) {
        TODO("Not yet implemented")
    }

    actual fun stopPlayingData() {
        TODO("Not yet implemented")
    }

}