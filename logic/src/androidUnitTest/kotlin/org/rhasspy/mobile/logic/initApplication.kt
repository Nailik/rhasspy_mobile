package org.rhasspy.mobile.logic

import org.rhasspy.mobile.android.AndroidApplication

actual fun initApplication() {
    AndroidApplication().onInit()
}