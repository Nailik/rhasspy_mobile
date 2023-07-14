package org.rhasspy.mobile.logic.services.dialog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.DialogManagementOption.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerDisabled
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerLocal
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerRemoteMqtt
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IDialogManagerService : IService {

    val dialogHistory: StateFlow<List<Pair<DialogServiceMiddlewareAction, DialogManagerState>>>

    override val serviceState: StateFlow<ServiceState>
    val currentDialogState: StateFlow<DialogManagerState>

    fun transitionTo(action: DialogServiceMiddlewareAction, state: DialogManagerState)

    fun onAction(action: DialogServiceMiddlewareAction)

    suspend fun informMqtt(sessionData: SessionData, action: DialogServiceMiddlewareAction)
}

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
internal class DialogManagerService(
    private val mqttService: IMqttService
) : IDialogManagerService {

    private val dialogManagerLocal by inject<DialogManagerLocal>()
    private val dialogManagerRemoteMqtt by inject<DialogManagerRemoteMqtt>()
    private val dialogManagerDisabled by inject<DialogManagerDisabled>()


    override val dialogHistory = MutableStateFlow<List<Pair<DialogServiceMiddlewareAction, DialogManagerState>>>(listOf())

    override val logger = LogType.DialogManagerService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _currentDialogState = MutableStateFlow<DialogManagerState>(IdleState())
    override val currentDialogState = _currentDialogState.readOnly

    init {
        _serviceState.value = ServiceState.Success
    }

    override fun transitionTo(action: DialogServiceMiddlewareAction, state: DialogManagerState) {
        //TODO record
        _currentDialogState.value = state
    }

    override fun onAction(action: DialogServiceMiddlewareAction) {
        coroutineScope.launch {
            when (ConfigurationSetting.dialogManagementOption.value) {
                Local -> dialogManagerLocal.onAction(action)
                RemoteMQTT -> dialogManagerRemoteMqtt.onAction(action)
                Disabled -> dialogManagerDisabled.onAction(action)
            }
        }
    }

    override suspend fun informMqtt(
        sessionData: SessionData,
        action: DialogServiceMiddlewareAction
    ) {
        if (action.source !is Source.Mqtt) {
            when (action) {
                is AsrError -> mqttService.asrError(sessionData.sessionId)
                is AsrTextCaptured -> mqttService.asrTextCaptured(sessionData.sessionId, action.text)
                is WakeWordDetected -> mqttService.hotWordDetected(action.wakeWord)
                is IntentRecognitionError -> mqttService.intentNotRecognized(sessionData.sessionId)
                is PlayFinished -> mqttService.playFinished()
                is SessionEnded -> mqttService.sessionEnded(sessionData.sessionId)
                is SessionStarted -> mqttService.sessionStarted(sessionData.sessionId)
                else -> {}
            }
        }
    }

}