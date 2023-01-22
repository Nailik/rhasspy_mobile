package org.rhasspy.mobile.nativeutils

/**
 * handles indication of wake up locally
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
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