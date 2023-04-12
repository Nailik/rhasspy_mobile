package org.rhasspy.mobile.platformspecific.media

expect object MediaUtils {

    fun requestAudioFocus()

    fun abandonAudioFocus()

}