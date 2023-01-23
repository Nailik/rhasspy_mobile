package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.services.httpclient.HttpClientResult
import org.rhasspy.mobile.services.httpclient.HttpClientService

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<HttpClientService>().serviceState

    private val speechToTextConfigurationTest = SpeechToTextConfigurationTest()
    val isRecording = speechToTextConfigurationTest.isRecording

    fun toggleRecording() {
        speechToTextConfigurationTest.toggleRecording()
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
                get<AudioPlayingService>().playAudio(result.data, false)
            }
        }
    }

}