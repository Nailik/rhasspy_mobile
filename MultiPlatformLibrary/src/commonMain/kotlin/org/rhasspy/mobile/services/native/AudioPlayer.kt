package org.rhasspy.mobile.services.native

expect object AudioPlayer {

    fun setEnabled(enabled: Boolean)

    fun playData(data: ByteArray)

}