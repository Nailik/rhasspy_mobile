package org.rhasspy.mobile.platformspecific.extensions

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.platformspecific.application.INativeApplication

expect fun FileResource.commonData(nativeApplication: INativeApplication): ByteArray