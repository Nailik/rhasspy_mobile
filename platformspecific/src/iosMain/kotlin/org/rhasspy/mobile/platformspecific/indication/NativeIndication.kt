package org.rhasspy.mobile.platformspecific.indication

actual object NativeIndication {

    /**
     * wake up screen if possible
     */
    actual fun wakeUpScreen() {
    }

    /**
     * remove wake lock and let screen go off
     */
    actual fun releaseWakeUp() {
    }

}