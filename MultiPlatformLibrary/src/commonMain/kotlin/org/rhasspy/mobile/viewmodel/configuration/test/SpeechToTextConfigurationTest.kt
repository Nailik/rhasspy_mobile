package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.settings.option.SpeechToTextOption

class SpeechToTextConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<SpeechToTextService>().serviceState
    val isRecording get() = get<RecordingService>().isRecording
    private val speechToTextService by inject<SpeechToTextService>()

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
                //start recording
                speechToTextService.startSpeechToText("")
            } else {
                //stop recording
                speechToTextService.endSpeechToText("", false)
            }
        }
    }

}