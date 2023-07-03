package org.rhasspy.mobile.platformspecific.extensions

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun FileResource.commonData(nativeApplication: INativeApplication): ByteArray =
    (nativeApplication as NativeApplication).resources.openRawResource(this.rawResId).readBytes()