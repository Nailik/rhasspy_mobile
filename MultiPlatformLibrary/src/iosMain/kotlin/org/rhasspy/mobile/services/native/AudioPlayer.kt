package org.rhasspy.mobile.services.native

actual object AudioPlayer {

    actual fun startStream(byteArray: ByteArray): AudioStreamInterface {

        return object : AudioStreamInterface {

            override fun enqueue(byteArray: ByteArray) {

            }

            override fun close() {

            }

        }
    }
}