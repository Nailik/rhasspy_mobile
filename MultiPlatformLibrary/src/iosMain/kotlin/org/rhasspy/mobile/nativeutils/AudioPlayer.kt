package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.observer.Observable

actual object AudioPlayer {

    actual fun playData(data: List<Byte>, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    actual val isPlayingState: Observable<Boolean>
        get() = TODO("Not yet implemented")

    actual fun playSoundFileResource(fileResource: FileResource) {
        TODO("Not yet implemented")
    }

    actual fun playSoundFile(filename: String) {
        TODO("Not yet implemented")
    }

}