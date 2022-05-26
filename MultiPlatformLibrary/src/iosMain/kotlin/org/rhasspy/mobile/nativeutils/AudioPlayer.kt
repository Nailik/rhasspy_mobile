package org.rhasspy.mobile.nativeutils

import com.badoo.reaktive.observable.Observable
import dev.icerock.moko.resources.FileResource

actual object AudioPlayer {

    actual fun playData(data: List<Byte>) {
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