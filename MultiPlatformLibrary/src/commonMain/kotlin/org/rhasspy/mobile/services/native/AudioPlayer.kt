package org.rhasspy.mobile.services.native

expect object AudioPlayer {

    fun playData(data: ByteArray)

    fun playRecording(data: ByteArray)

}