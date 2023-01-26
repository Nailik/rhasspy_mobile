package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.setCrashlyticsUnhandledExceptionHook

actual fun Logger.unhandledExceptionHook() {
    setCrashlyticsUnhandledExceptionHook()
}