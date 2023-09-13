package org.rhasspy.mobile.platformspecific.toast

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun NativeApplication.shortToast(resource: StringResource) {
    //TODO #518
}

actual fun NativeApplication.longToast(resource: StringResource) {
    //TODO #518
}