package org.rhasspy.mobile.viewmodel.screens.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.ui.event.StateEvent.Consumed
import org.rhasspy.mobile.ui.event.StateEvent.Triggered
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

class ConfigurationScreenViewModel(
    httpClientService: HttpClientService,
    webServerService: WebServerService,
    mqttService: MqttService,
    wakeWordService: WakeWordService,
    speechToTextService: SpeechToTextService,
    intentRecognitionService: IntentRecognitionService,
    textToSpeechService: TextToSpeechService,
    audioPlayingService: AudioPlayingService,
    dialogManagerService: DialogManagerService,
    intentHandlingService: IntentHandlingService
) : ViewModel() {

    private val scrollToErrorEvent = MutableStateFlow(ScrollToErrorEventIState(Consumed, 0))

    private val _viewState = MutableStateFlow(
        ConfigurationScreenViewState.getInitialViewState(
            httpClientService = httpClientService,
            webServerService = webServerService,
            mqttService = mqttService,
            wakeWordService = wakeWordService,
            speechToTextService = speechToTextService,
            intentRecognitionService = intentRecognitionService,
            textToSpeechService = textToSpeechService,
            audioPlayingService = audioPlayingService,
            dialogManagerService = dialogManagerService,
            intentHandlingService = intentHandlingService
        )
    )

    val viewState = _viewState.readOnly

    fun onEvent(event: ConfigurationScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SiteIdChange -> {
                    ConfigurationSetting.siteId.value = change.text
                    it.copy(siteId = it.siteId.copy(text = change.text))
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ScrollToError ->
                viewState.value.firstErrorIndex.value?.also { firstErrorIndex ->
                    scrollToErrorEvent.value = ScrollToErrorEventIState(
                        stateEvent = Triggered,
                        firstErrorIndex = firstErrorIndex
                    )
                }
        }
    }

    fun onConsumed(event: IConfigurationScreenUiStateEvent) {
        when (event) {
            is ScrollToErrorEventIState -> scrollToErrorEvent.update { it.copy(stateEvent = Consumed) }
        }
    }

}