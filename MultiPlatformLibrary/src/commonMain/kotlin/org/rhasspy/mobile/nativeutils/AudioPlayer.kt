package org.rhasspy.mobile.nativeutils

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.publish.PublishSubject
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.services.IndicationState

expect object AudioPlayer {

    val isPlayingState: Observable<Boolean>

    fun playData(data: List<Byte>)

    fun playSoundFileResource(fileResource: FileResource)

    fun playSoundFile(filename: String)

}