package org.rhasspy.mobile.platformspecific.indication

actual object NativeIndication {

    /**
     * wake up screen if possible
     */
    actual fun wakeUpScreen() {
        //TODO #514
    }

    /**
     * remove wake lock and let screen go off
     */
    actual fun releaseWakeUp() {
        //TODO #514
    }

}