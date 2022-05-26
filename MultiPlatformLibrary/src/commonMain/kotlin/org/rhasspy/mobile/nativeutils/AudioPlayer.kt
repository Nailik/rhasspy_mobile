package org.rhasspy.mobile.nativeutils

import com.badoo.reaktive.observable.Observable
import dev.icerock.moko.resources.FileResource

expect object AudioPlayer {

    val isPlayingState: Observable<Boolean>

    fun playData(data: List<Byte>)

    fun playSoundFileResource(fileResource: FileResource)

    fun playSoundFile(filename: String)

}