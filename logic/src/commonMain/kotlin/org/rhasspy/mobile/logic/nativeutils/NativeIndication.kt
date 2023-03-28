package org.rhasspy.mobile.logic.nativeutils

/**
 * handles indication of wake up locally
 */
expect object NativeIndication {

    /**
     * wake up screen if possible
     */
    fun wakeUpScreen()

    /**
     * remove wake lock and let screen go off
     */
    fun releaseWakeUp()

}