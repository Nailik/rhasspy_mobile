package org.rhasspy.mobile.logic.nativeutils

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual class AudioRecorder : Closeable {
    /**
     * output data as flow
     */
    actual val output: Flow<List<Byte>>
        get() = TODO("Not yet implemented")

    /**
     * max volume since start recording
     */
    actual val maxVolume: StateFlow<Short>
        get() = TODO("Not yet implemented")
    actual val isRecording: StateFlow<Boolean>
        get() = TODO("Not yet implemented")
    actual val absoluteMaxVolume: Double
        get() = TODO("Not yet implemented")

    /**
     * start recording
     */
    actual fun startRecording() {
    }

    /**
     * stop recording
     */
    actual fun stopRecording() {
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}