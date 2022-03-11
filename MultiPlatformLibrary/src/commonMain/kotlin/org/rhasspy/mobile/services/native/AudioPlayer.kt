package org.rhasspy.mobile.services.native

import io.ktor.utils.io.bits.*

expect object AudioPlayer {

    fun startStream(byteArray: ByteArray): AudioStreamInterface

}

interface AudioStreamInterface {
    fun enqueue(byteArray: ByteArray)
}