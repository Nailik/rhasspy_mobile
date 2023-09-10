package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.domains.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

class ConfigurationScreenViewStateCreator(
    dispatcherProvider: IDispatcherProvider,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
    private val rhasspy3WyomingConnection: IRhasspy3WyomingConnection,
    private val homeAssistantConnection: IHomeAssistantConnection,
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val wakeWordService: IWakeWordService,
    private val speechToTextService: ISpeechToTextService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val textToSpeechService: ITextToSpeechService,
    private val audioPlayingService: IAudioPlayingService,
    private val dialogManagerService: IDialogManagerService,
    private val intentHandlingService: IIntentHandlingService,
) {
    private val hasConnectionError = combineStateFlow(
        rhasspy2HermesConnection.connectionState,
        rhasspy3WyomingConnection.connectionState,
        homeAssistantConnection.connectionState,
        mqttConnection.connectionState,
        webServerConnection.connectionState,
    ).mapReadonlyState { arr ->
        arr.any { it is ServiceState.ErrorState }
    }

    private val hasError = combineStateFlow(
        rhasspy2HermesConnection.connectionState,
        rhasspy3WyomingConnection.connectionState,
        homeAssistantConnection.connectionState,
        mqttConnection.connectionState,
        webServerConnection.connectionState,
        wakeWordService.serviceState,
        speechToTextService.serviceState,
        intentRecognitionService.serviceState,
        textToSpeechService.serviceState,
        audioPlayingService.serviceState,
        dialogManagerService.serviceState,
        intentHandlingService.serviceState
    ).mapReadonlyState { arr ->
        arr.any { it is ServiceState.ErrorState }
    }

    private val viewState = MutableStateFlow(getViewState())

    init {
        combineStateFlow(
            hasConnectionError,
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.dialogManagementOption.data,
            ConfigurationSetting.wakeWordOption.data,
            ConfigurationSetting.speechToTextOption.data,
            ConfigurationSetting.intentRecognitionOption.data,
            ConfigurationSetting.textToSpeechOption.data,
            ConfigurationSetting.audioPlayingOption.data,
            ConfigurationSetting.intentHandlingOption.data,
        ).mapReadonlyState {
            viewState.value = getViewState()
        }
    }

    operator fun invoke(): MutableStateFlow<ConfigurationScreenViewState> = viewState

    private fun getViewState(): ConfigurationScreenViewState {
        return ConfigurationScreenViewState(
            siteId = SiteIdViewState(
                text = ConfigurationSetting.siteId.data
            ),
            connectionsViewState = ConnectionsViewState(
                hasError = hasConnectionError.value
            ),
            dialogPipeline = DialogPipelineViewState(
                dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
                serviceState = ServiceViewState(dialogManagerService.serviceState)
            ),
            audioInput = AudioInputViewState(
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO
            ),
            wakeWord = WakeWordViewState(
                wakeWordValueOption = ConfigurationSetting.wakeWordOption.value,
                serviceState = ServiceViewState(wakeWordService.serviceState)
            ),
            speechToText = SpeechToTextViewState(
                speechToTextOption = ConfigurationSetting.speechToTextOption.value,
                serviceState = ServiceViewState(speechToTextService.serviceState)
            ),
            voiceActivityDetection = VoiceActivityDetectionViewState(
                voiceActivityDetectionOption = VoiceActivityDetectionOption.Disabled,
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO
            ),
            intentRecognition = IntentRecognitionViewState(
                intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
                serviceState = ServiceViewState(intentRecognitionService.serviceState)
            ),
            intentHandling = IntentHandlingViewState(
                intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
                serviceState = ServiceViewState(intentHandlingService.serviceState)
            ),
            textToSpeech = TextToSpeechViewState(
                textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
                serviceState = ServiceViewState(textToSpeechService.serviceState)
            ),
            audioPlaying = AudioPlayingViewState(
                audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
                serviceState = ServiceViewState(audioPlayingService.serviceState)
            ),
            hasError = hasError
        )
    }


}