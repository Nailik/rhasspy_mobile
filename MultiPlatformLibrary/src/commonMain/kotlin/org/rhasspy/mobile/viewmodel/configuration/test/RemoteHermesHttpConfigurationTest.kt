package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.option.IntentRecognitionOption
import org.rhasspy.mobile.settings.option.SpeechToTextOption
import org.rhasspy.mobile.settings.option.TextToSpeechOption

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<HttpClientService>().currentState

    fun startTest() {
        val client = get<HttpClientService>()
        val rhasspyActionsServiceParams = get<RhasspyActionsServiceParams>()
        testScope.launch {
            if (rhasspyActionsServiceParams.speechToTextOption == SpeechToTextOption.RemoteHTTP) {
                client.speechToText(emptyList())
            }
            if (rhasspyActionsServiceParams.intentRecognitionOption == IntentRecognitionOption.RemoteHTTP) {
                client.recognizeIntent("text")
            }
            if (rhasspyActionsServiceParams.textToSpeechOption == TextToSpeechOption.RemoteHTTP) {
                client.textToSpeech("text")
            }
        }
    }

    override fun onClose() {
        //nothing to do
    }
}