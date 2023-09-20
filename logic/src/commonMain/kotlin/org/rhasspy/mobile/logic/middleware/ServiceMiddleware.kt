package org.rhasspy.mobile.logic.middleware

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import okio.Path
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.dialog.DialogManagerState.SessionState.RecordingIntentState
import org.rhasspy.mobile.logic.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.local.settings.IAppSettingsUtil
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IServiceMiddleware {

    val isUserActionEnabled: StateFlow<Boolean>
    val isPlayingRecording: StateFlow<Boolean>
    val isPlayingRecordingEnabled: StateFlow<Boolean>
    fun userSessionClick()

}
/**
 * handles ALL INCOMING events
 */
internal class ServiceMiddleware: IServiceMiddleware {
    override val isUserActionEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(true)
    override val isPlayingRecording: StateFlow<Boolean>
        get() = MutableStateFlow(false)
    override val isPlayingRecordingEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(false)


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