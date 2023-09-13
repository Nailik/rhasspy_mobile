package org.rhasspy.mobile.logic.domains.dialog

import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.domains.dialog.DialogInformation.Action
import org.rhasspy.mobile.logic.domains.dialog.DialogInformation.State
import org.rhasspy.mobile.logic.domains.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.disabled.DialogManagerDisabled
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.local.DialogManagerLocal
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.mqtt.DialogManagerMqtt
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IDialogManagerService : IService {

    val dialogHistory: StateFlow<List<DialogInformation>>

    override val serviceState: StateFlow<ServiceState>
    val currentDialogState: StateFlow<DialogManagerState>

    suspend fun onAction(action: DialogServiceMiddlewareAction)
    fun informMqtt(sessionData: SessionData?, action: DialogServiceMiddlewareAction)

    fun transitionTo(state: DialogManagerState)
    fun addToHistory(action: DialogServiceMiddlewareAction)
    fun clearHistory()

}

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
internal class DialogManagerService(
    dispatcherProvider: IDispatcherProvider,
    private val mqttService: IMqttConnection
) : IDialogManagerService {

    private val dialogManagerLocal by inject<DialogManagerLocal>()
    private val dialogManagerMqtt by inject<DialogManagerMqtt>()
    private val dialogManagerDisabled by inject<DialogManagerDisabled>()

    override val dialogHistory = MutableStateFlow<ImmutableList<DialogInformation>>(persistentListOf())

    private val logger = Logger.withTag("DialogManagerService")

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val _currentDialogState = MutableStateFlow<DialogManagerState>(IdleState)
    override val currentDialogState = _currentDialogState.readOnly

    private val scope = CoroutineScope(dispatcherProvider.IO)

    init {
        _serviceState.value = Success

        scope.launch {
            ConfigurationSetting.dialogManagementOption.data.collect {
                updateOptionAndInitServiceState(it)
            }
        }
    }

    private fun updateOptionAndInitServiceState(dialogManagementOption: DialogManagementOption) {
        dialogHistory.value = persistentListOf()
        _serviceState.value = when (dialogManagementOption) {
            DialogManagementOption.Local    -> {
                dialogManagerLocal.onInit()
                Success
            }

            DialogManagementOption.Rhasspy2HermesMQTT -> {
                dialogManagerMqtt.onInit()
                Success
            }

            DialogManagementOption.Disabled -> {
                dialogManagerDisabled.onInit()
                Disabled
            }
        }
    }

    override fun transitionTo(state: DialogManagerState) {
        _currentDialogState.value = state
        dialogHistory.value = dialogHistory.value.updateList {
            add(State(state))
            while (size > 100) {
                removeLast()
            }
        }
    }

    override fun addToHistory(action: DialogServiceMiddlewareAction) {
        dialogHistory.value = dialogHistory.value.updateList {
            add(Action(action))
            while (size > 100) {
                removeLast()
            }
        }
    }

    override suspend fun onAction(action: DialogServiceMiddlewareAction) {
        when (ConfigurationSetting.dialogManagementOption.value) {
            DialogManagementOption.Local              -> dialogManagerLocal.onAction(action)
            DialogManagementOption.Rhasspy2HermesMQTT -> dialogManagerMqtt.onAction(action)
            DialogManagementOption.Disabled           -> dialogManagerDisabled.onAction(action)
        }
    }

    override fun informMqtt(
        sessionData: SessionData?,
        action: DialogServiceMiddlewareAction
    ) {
        if (action.source !is Source.Mqtt) {
            if (sessionData != null) {
                when (action) {
                    is AsrError               -> mqttService.asrError(sessionData.sessionId)
                    is AsrTextCaptured        -> mqttService.asrTextCaptured(sessionData.sessionId, action.text)
                    is WakeWordDetected       -> mqttService.hotWordDetected(action.wakeWord)
                    is IntentRecognitionError -> mqttService.intentNotRecognized(sessionData.sessionId)
                    is SessionEnded           -> mqttService.sessionEnded(sessionData.sessionId)
                    is SessionStarted         -> mqttService.sessionStarted(sessionData.sessionId)
                    is PlayFinished           -> mqttService.playFinished()
                    else                      -> Unit
                }
            } else {
                when (action) {
                    is WakeWordDetected -> mqttService.hotWordDetected(action.wakeWord)
                    is PlayFinished     -> mqttService.playFinished()
                    else                -> Unit
                }
            }
        }

    }

    override fun clearHistory() {
        dialogHistory.value = persistentListOf()
    }

}