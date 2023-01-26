package org.rhasspy.mobile.logic.nativeutils

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

expect class AudioRecorder() : Closeable {

    /**
     * output data as flow
     */
    val output: Flow<ByteArray>

    /**
     * max volume since start recording
     */
    val maxVolume: StateFlow<Short>

    //state if currently recording
    val isRecording: StateFlow<Boolean>

    //maximum audio level that can happen
    val absoluteMaxVolume: Double

    /**
     * start recording
     */
    fun startRecording()

    /**
     * stop recording
     */
    fun stopRecording()

    companion object {
        /**
         * use the settings of the audio recorder
         * (samplingRate, channels, bitrate) and the audioSize
         * to create wav header and add it in front of the given data
         */
        fun ByteArray.appendWavHeader(): ByteArray
    }

}