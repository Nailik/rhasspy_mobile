package org.rhasspy.mobile.android

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation

fun revokePermission(permission: String) {
    getInstrumentation().uiAutomation.revokeRuntimePermission(
        getInstrumentation().targetContext.packageName, permission
    )
}
