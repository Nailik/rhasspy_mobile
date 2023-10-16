package org.rhasspy.mobile.platformspecific.extensions

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect fun FileResource.commonData(nativeApplication: NativeApplication): ByteArray

expect fun FileResource.readToString(nativeApplication: NativeApplication): String