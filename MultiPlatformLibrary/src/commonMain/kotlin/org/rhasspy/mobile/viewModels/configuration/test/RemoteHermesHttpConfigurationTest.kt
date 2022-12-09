package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<HttpClientService>().currentState

    fun startTest() {
        val client = get<HttpClientService>()
        val rhasspyActionsServiceParams = get<RhasspyActionsServiceParams>()
        testScope.launch {
            if (rhasspyActionsServiceParams.speechToTextOption == SpeechToTextOptions.RemoteHTTP) {
                client.speechToText(emptyList())
            }
            if (rhasspyActionsServiceParams.intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP) {
                client.recognizeIntent("text")
            }
            if (rhasspyActionsServiceParams.textToSpeechOption == TextToSpeechOptions.RemoteHTTP) {
                client.textToSpeech("text")
            }
        }
    }

    override fun onClose() {
        //nothing to do
    }
}