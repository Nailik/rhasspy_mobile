package org.rhasspy.mobile.services

import kotlin.native.concurrent.ThreadLocal

actual class NativeService {

    @ThreadLocal
    actual companion object {

        actual fun doAction(action: Action) {

        }

        actual var isRunning: Boolean = false

        actual fun stop() {
        }

    }

}