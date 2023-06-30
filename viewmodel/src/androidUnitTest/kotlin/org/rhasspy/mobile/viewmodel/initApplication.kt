package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.android.AndroidApplication

actual fun initApplication() {
    AndroidApplication().onInit()
}