package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableSharedFlow

actual object AudioRecorder {

    actual val output: MutableSharedFlow<List<Byte>>
        get() = TODO("Not yet implemented")

    actual fun startRecording() {
        TODO("Not yet implemented")
    }

    actual fun stopRecording() {
        TODO("Not yet implemented")
    }

}