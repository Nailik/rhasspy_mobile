package org.rhasspy.mobile.logic.nativeutils

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual class AudioRecorder : Closeable {

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

    /**
     * output data as flow
     */
    actual val output: Flow<ByteArray>
        get() = TODO("Not yet implemented")

    actual companion object {
        /**
         * use the settings of the audio recorder
         * (samplingRate, channels, bitrate) and the audioSize
         * to create wav header and add it in front of the given data
         */
        actual fun ByteArray.appendWavHeader(): ByteArray {
            TODO("Not yet implemented")
        }

        actual fun getWavHeader(audioSize: Long): ByteArray {
            TODO("Not yet implemented")
        }
    }

}