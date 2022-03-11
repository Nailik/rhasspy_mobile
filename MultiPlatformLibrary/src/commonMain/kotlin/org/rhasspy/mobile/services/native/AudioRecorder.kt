package org.rhasspy.mobile.services.native

import kotlinx.coroutines.flow.MutableSharedFlow

expect object AudioRecorder {

    val output: MutableSharedFlow<ByteArray>

    fun startRecording()

    fun stopRecording()

}