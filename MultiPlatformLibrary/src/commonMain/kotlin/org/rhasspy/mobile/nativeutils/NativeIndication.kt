package org.rhasspy.mobile.nativeutils

/**
 * handles indication of wake up locally
 */
expect object NativeIndication {

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