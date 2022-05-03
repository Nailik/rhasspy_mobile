package org.rhasspy.mobile.nativeutils

expect object AudioPlayer {

    fun playData(data: List<Byte>)

}