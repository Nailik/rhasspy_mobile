package org.rhasspy.mobile.platformspecific.utils

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun isDebug(): Boolean {
    return Platform.isDebugBinary
}