package org.rhasspy.mobile.logic.connections.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent.StartStopPlayRecording
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent.StartStopRhasspy
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.IPipelineManager

interface IUserConnection {

    val incomingMessages: Flow<UserConnectionEvent>

    val indicationState: StateFlow<IndicationState>
    val showVisualIndicationState: StateFlow<Boolean>

    val isPlayingRecording: StateFlow<Boolean>
    val isPlayingRecordingEnabled: StateFlow<Boolean>

    //user clicks microphone button
    fun sessionAction()

    fun playRecordedSound()

    fun playWakeSound()
    fun playErrorSound()

    fun stopPlaySound()

    fun playRecordingAction()


    val isPlayingState: StateFlow<Boolean>


    val rhasspy2HermesHttpConnectionState: StateFlow<ConnectionState>
    val rhasspy3WyomingConnectionState: StateFlow<ConnectionState>
    val homeAssistantConnectionState: StateFlow<ConnectionState>
    val webServerConnectionState: StateFlow<ConnectionState>
    val rhasspy2HermesMqttConnectionState: StateFlow<ConnectionState>

    val micDomainState: StateFlow<MicDomainState>
    val wakeDomainState: StateFlow<DomainState>

    val micDomainRecordingState: StateFlow<Boolean>
    val asrDomainRecordingState: StateFlow<Boolean>
}

internal class UserConnection(
    indication: IIndication,
    private val localAudioService: ILocalAudioPlayer,
    rhasspy2HermesConnection: IRhasspy2HermesConnection,
    rhasspy3WyomingConnection: IRhasspy3WyomingConnection,
    homeAssistantConnection: IHomeAssistantConnection,
    webServerConnection: IWebServerConnection,
    mqttService: IMqttConnection,
) : IUserConnection, KoinComponent {

    private val pipelineManager get() = get<IPipelineManager>() //TODO maybe single directional data flow?

    override val incomingMessages = MutableSharedFlow<UserConnectionEvent>()
    override val indicationState = indication.indicationState
    override val showVisualIndicationState = indication.isShowVisualIndication

    override val isPlayingRecording: StateFlow<Boolean> = MutableStateFlow(false)//TODO
    override val isPlayingRecordingEnabled: StateFlow<Boolean> = MutableStateFlow(false)//TODO

    override val rhasspy2HermesHttpConnectionState = rhasspy2HermesConnection.connectionState
    override val rhasspy3WyomingConnectionState = rhasspy3WyomingConnection.connectionState
    override val homeAssistantConnectionState = homeAssistantConnection.connectionState
    override val webServerConnectionState = webServerConnection.connectionState
    override val rhasspy2HermesMqttConnectionState = mqttService.connectionState

    override val micDomainState get() = pipelineManager.micDomainStateFlow
    override val wakeDomainState get() = pipelineManager.wakeDomainStateFlow

    override val micDomainRecordingState get() = pipelineManager.micDomainRecordingStateFlow
    override val asrDomainRecordingState get() = pipelineManager.asrDomainRecordingStateFlow

    override val isPlayingState: StateFlow<Boolean> = localAudioService.isPlayingState

    override fun sessionAction() {
        incomingMessages.tryEmit(StartStopRhasspy)
    }

    override fun playRecordingAction() {
        incomingMessages.tryEmit(StartStopPlayRecording)
    }

    override fun playRecordedSound() {
        localAudioService.playRecordedSound()
    }

    override fun playWakeSound() {
        localAudioService.playWakeSound()
    }

    override fun playErrorSound() {
        localAudioService.playErrorSound()
    }

    override fun stopPlaySound() {
        localAudioService.stop()
    }


}