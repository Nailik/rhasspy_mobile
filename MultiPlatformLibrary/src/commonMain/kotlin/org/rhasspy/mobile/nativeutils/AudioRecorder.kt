package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.services.ServiceResponse

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AudioRecorder {

    val output: StateFlow<List<Byte>>
    val maxVolume: StateFlow<Short>
    val isRecording: StateFlow<Boolean>

    val absoluteMaxVolume: Double

    fun startRecording() : ServiceResponse<*>

    fun stopRecording()

}