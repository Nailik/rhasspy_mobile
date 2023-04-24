package org.rhasspy.mobile.platformspecific.utils

import org.rhasspy.mobile.platformspecific.BuildConfig

actual fun isDebug(): Boolean {
    return BuildConfig.IS_DEBUG
}