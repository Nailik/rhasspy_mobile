package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.httpclient.HttpClientService

class RemoteHermesHttpConfigurationTest : IConfigurationTest() {

    public fun startTest() {
        val client = get<HttpClientService>()
        testScope.launch {
            client.speechToText(emptyList())
            client.recognizeIntent("text")
            client.textToSpeech("text")
        }
    }

    override fun onClose() {
        //nothing to do
    }
}