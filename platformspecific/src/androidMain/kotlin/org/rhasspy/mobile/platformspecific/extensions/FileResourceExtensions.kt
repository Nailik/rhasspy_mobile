package org.rhasspy.mobile.platformspecific.extensions

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun FileResource.commonData(nativeApplication: NativeApplication): ByteArray =
    nativeApplication.resources.openRawResource(this.rawResId).readBytes()

actual fun FileResource.readToString(nativeApplication: NativeApplication): String =
    this.readText(nativeApplication)