package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.platformspecific.application.IosApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun initApplication(): NativeApplication {
    return IosApplication().apply {
        onInit()
    }
}