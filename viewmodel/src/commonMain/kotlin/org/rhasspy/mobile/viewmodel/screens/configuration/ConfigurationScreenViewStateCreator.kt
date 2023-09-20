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
import org.rhasspy.mobile.logic.domains.audioplaying.ISndDomain
import org.rhasspy.mobile.logic.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

class ConfigurationScreenViewStateCreator(
    rhasspy2HermesConnection: IRhasspy2HermesConnection,
    rhasspy3WyomingConnection: IRhasspy3WyomingConnection,
    homeAssistantConnection: IHomeAssistantConnection,
    mqttConnection: IMqttConnection,
    webServerConnection: IWebServerConnection,
    private val wakeWordService: IWakeDomain,
    private val speechToTextService: IAsrDomain,
    private val intentRecognitionService: IIntentDomain,
    private val textToSpeechService: ITtsDomain,
    private val audioPlayingService: ISndDomain,
    private val dialogManagerService: IDialogManagerService,
    private val intentHandlingService: IHandleDomain,
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
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO #466
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
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO #469
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