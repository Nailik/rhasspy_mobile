package org.rhasspy.mobile.platformspecific.resource

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun FileResource.readToString(nativeApplication: INativeApplication): String = this.readText(nativeApplication as NativeApplication)