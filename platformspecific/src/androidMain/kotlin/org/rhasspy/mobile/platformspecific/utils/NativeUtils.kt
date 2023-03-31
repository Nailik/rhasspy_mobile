package org.rhasspy.mobile.platformspecific.utils

import androidx.multidex.BuildConfig

actual fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}