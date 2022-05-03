package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource

/**
 * handles indication of wake up locally
 */
expect object NativeIndication {

    /**
     * play audio resource
     */
    fun playSoundFileResource(fileResource: FileResource)

    /**
     * play some sound file
     */
    fun playSoundFile(filename: String)

    /**
     * wake up screen as long as possible
     */
    fun wakeUpScreen()

    /**
     * remote wake up and let screen go off
     */
    fun releaseWakeUp()

    /**
     * display indication over other apps
     */
    fun showIndication()

    /**
     * close indication over other apps
     */
    fun closeIndicationOverOtherApps()

}