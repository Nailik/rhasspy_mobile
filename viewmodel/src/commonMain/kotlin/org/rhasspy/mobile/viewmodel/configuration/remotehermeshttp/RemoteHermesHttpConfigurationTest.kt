package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationTest

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
                @Suppress("DEPRECATION")
                get<AudioPlayingService>().playAudio(AudioSource.Data(result.data))
            }
        }
    }

}