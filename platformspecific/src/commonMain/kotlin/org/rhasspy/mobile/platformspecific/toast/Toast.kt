package org.rhasspy.mobile.platformspecific.toast

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect fun NativeApplication.shortToast(resource: StringResource)
expect fun NativeApplication.longToast(resource: StringResource)