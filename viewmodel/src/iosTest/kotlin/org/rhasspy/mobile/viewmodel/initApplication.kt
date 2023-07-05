package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.IosApplication

actual fun initApplication(): INativeApplication {
    return IosApplication().apply {
        onInit()
    }
}