package org.rhasspy.mobile.audio

import dev.icerock.moko.resources.FileResource

expect object Audio {

     fun play(fileResource: FileResource)

}