package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

class ConfigurationScreenViewStateCreator(
    private val httpClientService: HttpClientService,
    private val webServerService: WebServerService,
    private val mqttService: MqttService,
    private val wakeWordService: WakeWordService,
    private val speechToTextService: SpeechToTextService,
    private val intentRecognitionService: IntentRecognitionService,
    private val textToSpeechService: TextToSpeechService,
    private val audioPlayingService: AudioPlayingService,
    private val dialogManagerService: DialogManagerService,
    private val intentHandlingService: IntentHandlingService
) {
    private val updaterScope = CoroutineScope(Dispatchers.IO)

    private val serviceStateFlow = combineStateFlow(
        httpClientService.serviceState,
        webServerService.serviceState,
        mqttService.serviceState,
        wakeWordService.serviceState,
        speechToTextService.serviceState,
        intentRecognitionService.serviceState,
        textToSpeechService.serviceState,
        audioPlayingService.serviceState,
        dialogManagerService.serviceState,
        intentHandlingService.serviceState
    )
    private val firstErrorIndex = serviceStateFlow.mapReadonlyState(sharingStarted = SharingStarted.Eagerly) { array ->
        val index = array.indexOfFirst { it is ServiceState.Error || it is ServiceState.Exception }
        return@mapReadonlyState if (index != -1) index else null
    }
    private val hasError = firstErrorIndex.mapReadonlyState { it != null }

    private val viewState = MutableStateFlow(getViewState(null))

    operator fun invoke(): MutableStateFlow<ConfigurationScreenViewState> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.siteId.data,
                ConfigurationSetting.isHttpClientSSLVerificationDisabled.data,
                ConfigurationSetting.isHttpServerEnabled.data,
                ConfigurationSetting.wakeWordOption.data,
                ConfigurationSetting.speechToTextOption.data,
                ConfigurationSetting.intentRecognitionOption.data,
                ConfigurationSetting.textToSpeechOption.data,
                ConfigurationSetting.audioPlayingOption.data,
                ConfigurationSetting.dialogManagementOption.data,
                ConfigurationSetting.intentHandlingOption.data,
                mqttService.isConnected
            ).collect {
                viewState.value = getViewState(viewState.value.scrollToError)
            }
        }

        return viewState
    }

    private fun getViewState(scrollToError: Int?): ConfigurationScreenViewState {
        return ConfigurationScreenViewState(
            siteId = SiteIdViewState(
                text = ConfigurationSetting.siteId.data
            ),
            remoteHermesHttp = RemoteHermesHttpViewState(
                isHttpSSLVerificationEnabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
                serviceState = ServiceViewState(httpClientService.serviceState)
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.value,
                serviceState = ServiceViewState(webServerService.serviceState)
            ),
            mqtt = MqttViewState(
                isMQTTConnected = mqttService.isConnected.value,
                serviceState = ServiceViewState(mqttService.serviceState)
            ),
            wakeWord = WakeWordViewState(
                wakeWordValueOption = ConfigurationSetting.wakeWordOption.value,
                serviceState = ServiceViewState(wakeWordService.serviceState)
            ),
            speechToText = SpeechToTextViewState(
                speechToTextOption = ConfigurationSetting.speechToTextOption.value,
                serviceState = ServiceViewState(speechToTextService.serviceState)
            ),
            intentRecognition = IntentRecognitionViewState(
                intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
                serviceState = ServiceViewState(intentRecognitionService.serviceState)
            ),
            textToSpeech = TextToSpeechViewState(
                textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
                serviceState = ServiceViewState(textToSpeechService.serviceState)
            ),
            audioPlaying = AudioPlayingViewState(
                audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
                serviceState = ServiceViewState(audioPlayingService.serviceState)
            ),
            dialogManagement = DialogManagementViewState(
                dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
                serviceState = ServiceViewState(dialogManagerService.serviceState)
            ),
            intentHandling = IntentHandlingViewState(
                intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
                serviceState = ServiceViewState(intentHandlingService.serviceState)
            ),
            hasError = hasError,
            firstErrorIndex = firstErrorIndex,
            scrollToError = scrollToError
        )
    }


}