package org.rhasspy.mobile.logic.nativeutils

import androidx.multidex.BuildConfig

actual fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}