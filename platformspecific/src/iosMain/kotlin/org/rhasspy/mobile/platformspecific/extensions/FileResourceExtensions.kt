package org.rhasspy.mobile.platformspecific.extensions

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun FileResource.commonData(nativeApplication: NativeApplication): ByteArray {
    //TODO #514
    return ByteArray(0)
}