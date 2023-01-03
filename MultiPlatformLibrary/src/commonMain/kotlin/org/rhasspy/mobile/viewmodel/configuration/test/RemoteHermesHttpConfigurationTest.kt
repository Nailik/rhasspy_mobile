package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.settings.option.IntentRecognitionOption
import org.rhasspy.mobile.settings.option.SpeechToTextOption
import org.rhasspy.mobile.settings.option.TextToSpeechOption

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<HttpClientService>().serviceState

    fun startTest() {
        val client = get<HttpClientService>()
        testScope.launch {
            if (get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteHTTP) {
                client.speechToText(emptyList())
            }
            if (get<IntentRecognitionServiceParams>().intentRecognitionOption == IntentRecognitionOption.RemoteHTTP) {
                client.recognizeIntent("text")
            }
            if (get<TextToSpeechServiceParams>().textToSpeechOption == TextToSpeechOption.RemoteHTTP) {
                client.textToSpeech("text")
            }
        }
    }

    override fun onClose() {
        //nothing to do
    }
}