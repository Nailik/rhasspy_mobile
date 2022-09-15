package org.rhasspy.mobile.nativeutils

/**
 * handles indication of wake up locally
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NativeIndication {

    /**
     * wake up screen as long as possible
     */
    fun wakeUpScreen()

    /**
     * remote wake up and let screen go off
     */
    fun releaseWakeUp()

}