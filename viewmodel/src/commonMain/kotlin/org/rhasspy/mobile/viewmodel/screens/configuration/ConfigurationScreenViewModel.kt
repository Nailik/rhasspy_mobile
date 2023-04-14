package org.rhasspy.mobile.viewmodel.screens.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
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
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.ui.event.StateEvent.Consumed
import org.rhasspy.mobile.ui.event.StateEvent.Triggered
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiAction.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiAction.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.ScrollToErrorEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.AudioPlayingViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.DialogManagementViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.IntentRecognitionViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.MqttViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.RemoteHermesHttpViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.SiteIdViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.SpeechToTextViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.TextToSpeechViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.WakeWordViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.WebServerViewState

class ConfigurationScreenViewModel : ViewModel(), KoinComponent {

    private val serviceStateFlow = combineStateFlow(
        get<HttpClientService>().serviceState,
        get<WebServerService>().serviceState,
        get<MqttService>().serviceState,
        get<WakeWordService>().serviceState,
        get<SpeechToTextService>().serviceState,
        get<IntentRecognitionService>().serviceState,
        get<TextToSpeechService>().serviceState,
        get<AudioPlayingService>().serviceState,
        get<DialogManagerService>().serviceState,
        get<IntentHandlingService>().serviceState
    ) { it }

    private val scrollToErrorEvent = MutableStateFlow(ScrollToErrorEvent(Consumed, 0))

    private val _viewState = MutableStateFlow(
        ConfigurationScreenViewState(
            siteId = SiteIdViewState(
                text = ConfigurationSetting.siteId.data
            ),
            remoteHermesHttp = RemoteHermesHttpViewState(
                isHttpSSLVerificationEnabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
                serviceState = ServiceViewState(get<HttpClientService>().serviceState)
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.value,
                serviceState = ServiceViewState(get<WebServerService>().serviceState)
            ),
            mqtt = MqttViewState(
                isMQTTConnected = get<MqttService>().isConnected,
                serviceState = ServiceViewState(get<MqttService>().serviceState)
            ),
            wakeWord = WakeWordViewState(
                wakeWordValueOption = ConfigurationSetting.wakeWordOption.value,
                serviceState = ServiceViewState(get<WakeWordService>().serviceState)
            ),
            speechToText = SpeechToTextViewState(
                speechToTextOption = ConfigurationSetting.speechToTextOption.value,
                serviceState = ServiceViewState(get<SpeechToTextService>().serviceState)
            ),
            intentRecognition = IntentRecognitionViewState(
                intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
                serviceState = ServiceViewState(get<IntentRecognitionService>().serviceState)
            ),
            textToSpeech = TextToSpeechViewState(
                textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
                serviceState = ServiceViewState(get<TextToSpeechService>().serviceState)
            ),
            audioPlaying = AudioPlayingViewState(
                audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
                serviceState = ServiceViewState(get<AudioPlayingService>().serviceState)
            ),
            dialogManagement = DialogManagementViewState(
                dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
                serviceState = ServiceViewState(get<DialogManagerService>().serviceState)
            ),
            intentHandling = ConfigurationScreenViewState.IntentHandlingViewState(
                intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
                serviceState = ServiceViewState(get<IntentHandlingService>().serviceState)
            ),
            hasError = serviceStateFlow.mapReadonlyState { array -> array.firstOrNull { it is ServiceState.Error } != null },
            scrollToErrorEvent = scrollToErrorEvent
        )
    )

    val viewState = _viewState.readOnly

    fun onAction(action: ConfigurationScreenUiAction) {
        when (action) {
            is SiteIdChange -> ConfigurationSetting.siteId.value = action.text
            ScrollToError -> scrollToErrorEvent.value = ScrollToErrorEvent(
                stateEvent = Triggered,
                firstErrorIndex = serviceStateFlow.value.indexOfFirst { serviceState -> serviceState is ServiceState.Error }
            )
        }
    }

    fun onConsumed(event: ConfigurationScreenUiEvent) {
        when (event) {
            is ScrollToErrorEvent -> scrollToErrorEvent.update { it.copy(stateEvent = Consumed) }
        }
    }

}