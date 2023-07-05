package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun initApplication(): NativeApplication {
    return AndroidApplication().apply {
        onInit()
    }
}