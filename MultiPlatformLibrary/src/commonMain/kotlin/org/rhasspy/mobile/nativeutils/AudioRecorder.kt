package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AudioRecorder {

    val output: MutableSharedFlow<List<Byte>>

    fun startRecording()

    fun stopRecording()

}