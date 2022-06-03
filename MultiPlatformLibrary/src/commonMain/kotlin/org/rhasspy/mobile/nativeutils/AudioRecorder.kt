package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableSharedFlow

expect object AudioRecorder {

    val output: MutableSharedFlow<List<Byte>>

    fun startRecording()

    fun stopRecording()

}