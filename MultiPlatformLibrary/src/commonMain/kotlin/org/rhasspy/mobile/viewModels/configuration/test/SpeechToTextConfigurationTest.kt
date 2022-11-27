package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.CoroutineScope

class SpeechToTextConfigurationTest : IConfigurationTest() {

    public override fun startTest() {
        super.startTest()
    }

    override fun runTest(scope: CoroutineScope) {
        //TODO("Not yet implemented")
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}


/*
    fun startTestRecording() {
val service = get<MqttService>()

if (!_isRecording.value) {
    _isRecording.value = true
    testScope = CoroutineScope(Dispatchers.Default)
    testScope.launch {
        service.hotWordDetected("test")
        //await start listening
        AudioRecorder.output.collect {
            if (_isRecording.value) {
                service.audioFrame(it.toMutableList().addWavHeader())
            }
        }
    }

    AudioRecorder.startRecording()
} else {
    testScope.launch {
        AudioRecorder.output.collect {
            if (!_isRecording.value) {
                //send silence to force stop recording
                //Works, fake silence
                service.audioFrame(it.map { 0.toByte() }.toMutableList().addWavHeader())
            }
        }
    }
}

 */