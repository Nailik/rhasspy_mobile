package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
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
    private val pipelineManager: IPipelineManager,
) {

    private val hasConnectionError = combineStateFlow(
        rhasspy2HermesConnection.connectionState,
        rhasspy3WyomingConnection.connectionState,
        homeAssistantConnection.connectionState,
        mqttConnection.connectionState,
        webServerConnection.connectionState,
    ).mapReadonlyState { arr ->
        arr.any { it is ConnectionState.ErrorState }
    }

    private val viewState = MutableStateFlow(getViewState())

    init {
        combineStateFlow(
            hasConnectionError,
            pipelineManager.micDomainStateFlow,
            pipelineManager.wakeDomainStateFlow,
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
                text = ConfigurationSetting.siteId.data,
            ),
            connectionsItemViewState = ConnectionsItemViewState(
                hasError = hasConnectionError.value,
            ),
            pipelineItemViewState = PipelineItemViewState(
                dialogManagementOption = ConfigurationSetting.pipelineData.value.option,
            ),
            micDomainItemViewState = MicDomainItemViewState(
                errorStateFlow = pipelineManager.micDomainStateFlow.mapReadonlyState { it.asDomainState() },
            ),
            wakeDomainItemViewState = WakeDomainItemViewState(
                wakeWordValueOption = ConfigurationSetting.wakeDomainData.value.wakeWordOption,
                errorStateFlow = pipelineManager.wakeDomainStateFlow,
            ),
            asrDomainItemViewState = AsrDomainItemViewState(
                speechToTextOption = ConfigurationSetting.asrDomainData.value.option,
            ),
            vadDomainItemViewState = VadDomainItemViewState(
                voiceActivityDetectionOption = VoiceActivityDetectionOption.Disabled,
            ),
            intentDomainItemViewState = IntentDomainItemViewState(
                intentRecognitionOption = ConfigurationSetting.intentDomainData.value.option,
            ),
            handleDomainItemViewState = HandleDomainItemViewState(
                intentHandlingOption = ConfigurationSetting.handleDomainData.value.option,
            ),
            ttsDomainItemViewState = TtsDomainItemViewState(
                textToSpeechOption = ConfigurationSetting.ttsDomainData.value.option,
            ),
            sndDomainItemViewState = SndDomainItemViewState(
                audioPlayingOption = ConfigurationSetting.sndDomainData.value.option,
            ),
        )
    }


}