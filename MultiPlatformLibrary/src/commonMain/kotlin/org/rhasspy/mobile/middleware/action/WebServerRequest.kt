package org.rhasspy.mobile.middleware.action

sealed interface WebServerRequest<T> {
    object PlayRecordingGet : WebServerRequest<ByteArray>
}