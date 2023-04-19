package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.updateViewState
import org.rhasspy.mobile.ui.event.StateEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

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
    private val updaterScope = CoroutineScope(Dispatchers.Default)

    private val scrollToErrorEvent = MutableStateFlow(ScrollToErrorEventIState(StateEvent.Consumed, 0))
    private val viewState = MutableStateFlow(getViewState())

    operator fun invoke(): StateFlow<ConfigurationScreenViewState> {
        updaterScope.launch {
            ConfigurationSetting.siteId.data.collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    fun updateScrollToError(stateEvent: StateEvent) {
        scrollToErrorEvent.value = scrollToErrorEvent.value.updateViewState {
            copy(
                stateEvent = stateEvent,
                firstErrorIndex = viewState.value.firstErrorIndex.value ?: firstErrorIndex
            )
        }
    }

    private fun getViewState(): ConfigurationScreenViewState {
        val serviceStateFlow = combineStateFlow(
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

        return ConfigurationScreenViewState(
            siteId = SiteIdViewState(
                text = ConfigurationSetting.siteId.value
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
                isMQTTConnected = mqttService.isConnected,
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
            hasError = serviceStateFlow.mapReadonlyState { array -> array.firstOrNull { it is ServiceState.Error } != null },
            firstErrorIndex = serviceStateFlow.mapReadonlyState { array -> array.indexOfFirst { it is ServiceState.Error } },
            scrollToErrorEvent = scrollToErrorEvent
        )
    }


}