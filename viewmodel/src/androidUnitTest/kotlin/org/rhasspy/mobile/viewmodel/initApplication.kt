package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.platformspecific.application.INativeApplication

actual fun initApplication(): INativeApplication {
    return AndroidApplication().apply {
        onInit()
    }
}