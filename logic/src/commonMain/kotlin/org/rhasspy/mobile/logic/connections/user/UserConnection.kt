package org.rhasspy.mobile.logic.connections.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.data.sounds.IndicationSoundType
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.DomainResult
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState

interface IUserConnection {

    val incomingMessages: Flow<UserConnectionEvent>

    val indicationState: StateFlow<IndicationState>
    val showVisualIndicationState: StateFlow<Boolean>

    val isWakeUpEnabled: StateFlow<Boolean>
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

    val pipelineHistory: StateFlow<List<DomainResult>>

    //user clicks microphone button
    fun sessionAction()

    suspend fun playSound(
        indicationSoundType: IndicationSoundType,
        soundIndicationOutputOption: AudioOutputOption,
        indicationSound: IndicationSoundOption,
        volume: Float,
    )

    fun stopPlaySound()

    fun playRecordingAction()

    fun clearPipelineHistory()
}

internal class UserConnection(
    indication: IIndication,
    private val localAudioService: ILocalAudioPlayer,
    private val domainHistory: IDomainHistory,
    rhasspy2HermesConnection: IRhasspy2HermesConnection,
    rhasspy3WyomingConnection: IRhasspy3WyomingConnection,
    homeAssistantConnection: IHomeAssistantConnection,
    webServerConnection: IWebServerConnection,
    mqttService: IMqttConnection,
) : IUserConnection, KoinComponent {

    private val pipelineManager get() = get<IPipelineManager>() //TODO move things to ui connection

    override val incomingMessages = MutableSharedFlow<UserConnectionEvent>()
    override val indicationState = indication.indicationState
    override val showVisualIndicationState = indication.isShowVisualIndication

    override val isWakeUpEnabled
        get() = combineState(pipelineManager.isPipelineActive, pipelineManager.isPlayingAudio) { isPipelineActive, isPlayingAudio ->
            !isPipelineActive && !isPlayingAudio
        }

    override val rhasspy2HermesHttpConnectionState = rhasspy2HermesConnection.connectionState
    override val rhasspy3WyomingConnectionState = rhasspy3WyomingConnection.connectionState
    override val homeAssistantConnectionState = homeAssistantConnection.connectionState
    override val webServerConnectionState = webServerConnection.connectionState
    override val rhasspy2HermesMqttConnectionState = mqttService.connectionState

    override val micDomainState get() = pipelineManager.micDomainStateFlow
    override val wakeDomainState get() = pipelineManager.wakeDomainStateFlow

    override val micDomainRecordingState get() = pipelineManager.micDomainRecordingStateFlow
    override val asrDomainRecordingState get() = pipelineManager.asrDomainRecordingStateFlow
    override val pipelineHistory: StateFlow<List<DomainResult>> = domainHistory.historyState

    override val isPlayingState: StateFlow<Boolean> get() = pipelineManager.isPlayingAudio

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun sessionAction() {
        if (isWakeUpEnabled.value) return

        scope.launch {
            incomingMessages.emit(UserConnectionEvent.StartStopRhasspy)
        }
    }

    override fun playRecordingAction() {
        scope.launch {
            incomingMessages.emit(UserConnectionEvent.StartStopPlayRecording)
        }
    }

    override fun clearPipelineHistory() {
        domainHistory.clearHistory()
    }

    override suspend fun playSound(
        indicationSoundType: IndicationSoundType,
        soundIndicationOutputOption: AudioOutputOption,
        indicationSound: IndicationSoundOption,
        volume: Float,
    ) {
        val audioSource = when (indicationSound) {
            is IndicationSoundOption.Custom   -> AudioSource.File(indicationSound.file.toPath())
            is IndicationSoundOption.Default  -> AudioSource.Resource(indicationSoundType.default)
            is IndicationSoundOption.Disabled -> return
        }

        localAudioService.playAudio(
            audioSource = audioSource,
            volume = volume,
            audioOutputOption = soundIndicationOutputOption
        )
    }

    override fun stopPlaySound() {
        localAudioService.stop()
    }


}