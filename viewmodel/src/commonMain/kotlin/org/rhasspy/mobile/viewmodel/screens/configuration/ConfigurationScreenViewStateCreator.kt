package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

class ConfigurationScreenViewStateCreator(
    private val userConnection: IUserConnection,
) {

    private val hasConnectionError = combineStateFlow(
        userConnection.rhasspy2HermesMqttConnectionState,
        userConnection.rhasspy2HermesMqttConnectionState,
        userConnection.rhasspy3WyomingConnectionState,
        userConnection.homeAssistantConnectionState,
        userConnection.webServerConnectionState,
    ).mapReadonlyState { arr ->
        arr.any { it is ConnectionState.ErrorState }
    }

    private val viewState = MutableStateFlow(getViewState())

    init {
        combineStateFlow(
            hasConnectionError,
            userConnection.micDomainState,
            userConnection.wakeDomainState,
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.pipelineData.data,
            ConfigurationSetting.wakeDomainData.data,
            ConfigurationSetting.asrDomainData.data,
            ConfigurationSetting.vadDomainData.data,
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
                pipelineManagerOption = ConfigurationSetting.pipelineData.value.option,
            ),
            micDomainItemViewState = MicDomainItemViewState(
                errorStateFlow = userConnection.micDomainState.mapReadonlyState { it.asDomainState() },
            ),
            wakeDomainItemViewState = WakeDomainItemViewState(
                wakeWordValueOption = ConfigurationSetting.wakeDomainData.value.wakeDomainOption,
                errorStateFlow = userConnection.wakeDomainState,
            ),
            asrDomainItemViewState = AsrDomainItemViewState(
                asrDomainOption = ConfigurationSetting.asrDomainData.value.option,
            ),
            vadDomainItemViewState = VadDomainItemViewState(
                vadDomainOption = ConfigurationSetting.vadDomainData.value.option,
            ),
            intentDomainItemViewState = IntentDomainItemViewState(
                intentDomainOption = ConfigurationSetting.intentDomainData.value.option,
            ),
            handleDomainItemViewState = HandleDomainItemViewState(
                handleDomainOption = ConfigurationSetting.handleDomainData.value.option,
            ),
            ttsDomainItemViewState = TtsDomainItemViewState(
                ttsDomainOption = ConfigurationSetting.ttsDomainData.value.option,
            ),
            sndDomainItemViewState = SndDomainItemViewState(
                sndDomainOption = ConfigurationSetting.sndDomainData.value.option,
            ),
        )
    }


}