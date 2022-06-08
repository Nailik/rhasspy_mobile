package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.observer.Observable

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AudioPlayer {

    val isPlayingState: Observable<Boolean>

    fun playData(data: List<Byte>, onFinished: () -> Unit)

    fun stopPlayingData()

    fun playSoundFileResource(fileResource: FileResource)

    fun playSoundFile(filename: String)


}