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
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.pipeline.IPipeline
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
    private val dialogManagerService: IPipeline,
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
    ).mapReadonlyState { arr ->
        arr.any { it is ServiceState.ErrorState }
    }

    private val viewState = MutableStateFlow(getViewState())

    init {
        combineStateFlow(
            hasConnectionError,
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.pipelineData.data,
            ConfigurationSetting.wakeDomainData.data,
            ConfigurationSetting.asrDomainData.data,
            ConfigurationSetting.intentDomainData.data,
            ConfigurationSetting.ttsDomainData.data,
            ConfigurationSetting.sndDomainData.data,
            ConfigurationSetting.handleDomainData.data,
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
                dialogManagementOption = ConfigurationSetting.pipelineData.value.option,
            ),
            audioInput = AudioInputViewState(
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO #466 mic
            ),
            wakeWord = WakeWordViewState(
                wakeWordValueOption = ConfigurationSetting.wakeDomainData.value.wakeWordOption,
                serviceState = ServiceViewState(wakeWordService.serviceState)
            ),
            speechToText = SpeechToTextViewState(
                speechToTextOption = ConfigurationSetting.asrDomainData.value.option,
            ),
            voiceActivityDetection = VoiceActivityDetectionViewState(
                voiceActivityDetectionOption = VoiceActivityDetectionOption.Disabled,
            ),
            intentRecognition = IntentRecognitionViewState(
                intentRecognitionOption = ConfigurationSetting.intentDomainData.value.option,
            ),
            intentHandling = IntentHandlingViewState(
                intentHandlingOption = ConfigurationSetting.handleDomainData.value.option,
            ),
            textToSpeech = TextToSpeechViewState(
                textToSpeechOption = ConfigurationSetting.ttsDomainData.value.option,
            ),
            audioPlaying = AudioPlayingViewState(
                audioPlayingOption = ConfigurationSetting.sndDomainData.value.option,
            ),
            hasError = hasError
        )
    }


}