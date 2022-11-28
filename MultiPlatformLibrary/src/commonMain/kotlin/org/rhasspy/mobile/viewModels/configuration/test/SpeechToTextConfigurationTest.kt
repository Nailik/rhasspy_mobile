package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

class SpeechToTextConfigurationTest : IConfigurationTest() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly


    public fun initialize(){
        get<MqttService>()
    }

    public fun toggleRecording() {

        val rhasspyActionsService = get<RhasspyActionsService>()

        if (!isRecording.value) {
            _isRecording.value = true
            testScope.launch {
                rhasspyActionsService.startSpeechToText("testsessionrecordingstt")
            }
        } else {
            _isRecording.value = false
            //execute
            testScope.launch {
                val response = rhasspyActionsService.endSpeechToText("testsessionrecordingstt")

                if(response is ServiceResponse.Success){
                    println(response.data)
                }
            }
        }
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}


/*
hermes/asr/toggleOn
hermes/asr/startListening

//send

//stop

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