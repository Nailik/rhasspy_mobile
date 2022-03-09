package org.rhasspy.mobile.services.native

import dev.icerock.moko.resources.FileResource

/**
 * handles indication of wake up locally
 */
expect object NativeIndication {

    /**
     * play audio resource
     */
    fun playAudio(fileResource: FileResource)

    /**
     * wake up screen as long as possible
     */
    fun wakeUpScreen()

    /**
     * remote wake up and let screen go off
     */
    fun releaseWakeUp()

    /**
     * acquire permission to draw over other apps
     */
    fun displayOverAppsPermission()

    /**
     * display indication over other apps
     */
    fun showIndication()

    /**
     * close indication over other apps
     */
    fun closeIndicationOverOtherApps()

}