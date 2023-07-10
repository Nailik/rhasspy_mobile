package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.Application
import org.rhasspy.mobile.platformspecific.application.NativeApplication

fun initApplication(): NativeApplication = Application().apply {
    onCreated()
}