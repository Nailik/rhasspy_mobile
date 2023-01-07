package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.settings.option.SpeechToTextOption

class SpeechToTextConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<SpeechToTextService>().serviceState
    val isRecording get() = get<RecordingService>().isRecording

    fun toggleRecording() {
        testScope.launch {
            if (get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            if (!isRecording.value) {
                println("not yet recording start")
                //start recording
                get<SpeechToTextService>().startSpeechToText("")
            } else {
                println("is recording, stop")
                //stop recording
                get<SpeechToTextService>().endSpeechToText("", false)
            }
        }
    }

}