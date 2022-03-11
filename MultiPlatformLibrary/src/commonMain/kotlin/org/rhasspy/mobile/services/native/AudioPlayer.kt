package org.rhasspy.mobile.services.native

expect object AudioPlayer {

    fun startStream(byteArray: ByteArray): AudioStreamInterface

}

interface AudioStreamInterface {

    fun enqueue(byteArray: ByteArray)

    fun close()

}