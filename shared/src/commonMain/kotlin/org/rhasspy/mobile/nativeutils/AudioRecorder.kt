package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class AudioRecorder() : Closeable {

    /**
     * output data as flow
     */
    val output: Flow<List<Byte>>

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

}