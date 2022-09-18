package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.services.ServiceAction

actual class NativeServiceInterop {

    @ThreadLocal
    actual companion object {

        actual var isRunning: Boolean = false

        actual fun doAction(serviceAction: ServiceAction) {
            TODO("Not yet implemented")
        }

        actual fun stop() {
            TODO("Not yet implemented")
        }

    }
}