package org.rhasspy.mobile.services.httpclient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.services.IServiceTest
import org.rhasspy.mobile.services.httpclient.data.HttpClientCallType

class HttpClientServiceTest(
    private val httpClientLink: HttpClientLink
) : IServiceTest<HttpClientCallType>("WebServerService", httpClientLink), KoinComponent {

    override fun onStartTest(scope: CoroutineScope) {
        pending(HttpClientCallType.TextToSpeech)
        pending(HttpClientCallType.SpeechToText)
        pending(HttpClientCallType.IntentRecognition)
        pending(HttpClientCallType.PlayWav)
        pending(HttpClientCallType.IntentHandling)
        pending(HttpClientCallType.HassEvent)
        pending(HttpClientCallType.HassIntent)
    }

    override fun runTest(scope: CoroutineScope) {
        scope.launch {
            loading(HttpClientCallType.TextToSpeech)
            httpClientLink.textToSpeech("TextToSpeech")
            loading(HttpClientCallType.SpeechToText)
            httpClientLink.speechToText(listOf())
            loading(HttpClientCallType.IntentRecognition)
            httpClientLink.intentRecognition("IntentRecognition")
            loading(HttpClientCallType.PlayWav)
            httpClientLink.playWav(listOf())
            loading(HttpClientCallType.IntentHandling)
            httpClientLink.intentHandling("IntentHandling")
            loading(HttpClientCallType.HassEvent)
            httpClientLink.hassEvent("{}", "HassEvent")
            loading(HttpClientCallType.HassIntent)
            httpClientLink.hassIntent("{}")
        }
    }

    override fun getService(): HttpClientService = get()

}