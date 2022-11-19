package org.rhasspy.mobile.services

import dev.icerock.moko.resources.StringResource

data class ServiceError<T>(val e: Throwable, val data: T, val description: StringResource)
