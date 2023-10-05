package org.rhasspy.mobile.logic.middleware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IServiceMiddleware {

    val isUserActionEnabled: StateFlow<Boolean>
    val isPlayingRecording: StateFlow<Boolean>
    val isPlayingRecordingEnabled: StateFlow<Boolean>
    fun userSessionClick()
    fun playRecording() {
        //TODO #466 ("Not yet implemented")
    }

}

/**
 * handles ALL INCOMING events
 */
internal class ServiceMiddleware : IServiceMiddleware {
    override val isUserActionEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(true)
    override val isPlayingRecording: StateFlow<Boolean>
        get() = MutableStateFlow(false)
    override val isPlayingRecordingEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    //TODO #466 listen and wait for wake
    override fun userSessionClick() {
        /*
        when (dialogManagerService.currentDialogState.value) {
            is IdleState            -> {
                if (isAnyServiceUsingMqtt() && !mqttService.isHasStarted.value) {
                    //await for mqtt to be started (connected and subscribed to topics) in the case that any service is using mqtt
                    //this is necessary to ensure all topics are correctly being sent and consumed
                    awaitMqttConnected?.cancel()
                    awaitMqttConnected = coroutineScope.launch {
                        mqttService.isHasStarted.first { it }
                        action(WakeWordDetected(Local, "manual"))
                    }
                } else {
                    action(WakeWordDetected(Local, "manual"))
                }
            }

            is RecordingIntentState -> action(StopListening(Local))
            else                    -> Unit
        }*/
    }

}