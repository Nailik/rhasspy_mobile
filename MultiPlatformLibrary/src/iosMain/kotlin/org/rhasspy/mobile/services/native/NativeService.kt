package org.rhasspy.mobile.services.native

import org.rhasspy.mobile.services.Action

actual class NativeService {

    @ThreadLocal
    actual companion object {

        actual var isRunning: Boolean = false

        actual fun doAction(action: Action) {

        }

        actual fun stop() {

        }

    }
}