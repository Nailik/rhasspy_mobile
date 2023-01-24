package org.rhasspy.mobile.logic.nativeutils

import org.rhasspy.mobile.logic.BuildConfig

actual fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}