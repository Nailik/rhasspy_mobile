package org.rhasspy.mobile.platformspecific.resource

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun FileResource.readToString(nativeApplication: NativeApplication): String = this.readText(nativeApplication)