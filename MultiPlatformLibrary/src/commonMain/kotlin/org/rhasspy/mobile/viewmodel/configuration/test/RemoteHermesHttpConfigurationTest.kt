package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.services.httpclient.HttpClientResult
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<HttpClientService>().serviceState
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    fun startSpeechToTextTest() {
        //TODO??
        val speechToTextService = get<SpeechToTextService>()
        //  rhasspyActionsService.startSpeechToText(middleware.sessionId)
    }

    fun startIntentRecognitionTest(text: String) {
        testScope.launch {
            get<HttpClientService>().recognizeIntent(text)
        }
    }

    fun startTextToSpeechTest(text: String) {
        testScope.launch {
            val result = get<HttpClientService>().textToSpeech(text)
            if (result is HttpClientResult.Success) {
                get<AudioPlayingService>().playAudio(result.data.toList())
            }
        }
    }

}