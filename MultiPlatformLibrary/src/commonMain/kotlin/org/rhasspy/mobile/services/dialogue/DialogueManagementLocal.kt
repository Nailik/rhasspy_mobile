package org.rhasspy.mobile.services.dialogue

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.services.DialogueAction
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

object DialogueManagementLocal : IDialogueManagement() {

    override suspend fun doAction(action: DialogueAction) {
        when (action) {
            DialogueAction.HotWordDetected -> {
                //start Session
                doAction(DialogueAction.StartSession)
            }
            DialogueAction.StartSession -> {
                //create session id
                sessionId = uuid4().toString()
                //start listening
                doAction(DialogueAction.StartListening)
                //publish that session has been started onto mqtt
                publishActionOnMQTT(DialogueAction.SessionStarted)
            }
            DialogueAction.StartListening -> {
                //shows indication and starts recording
                ServiceInterface.startRecording()
            }
            DialogueAction.ToggleHotWordOff -> {
                ServiceInterface.setWakeWordEnabled(false)
            }
            DialogueAction.SessionStarted -> TODO()
            DialogueAction.ToggleHotWordOn -> TODO()
            DialogueAction.StopListening -> TODO()
            DialogueAction.SessionEnded -> TODO()
            else -> {}
        }
    }


    /**
     * send that some action was done to mqtt broker
     * only if mqtt is enabled
     */
    private fun publishActionOnMQTT(action: DialogueAction) {
        if (ConfigurationSettings.isMQTTEnabled.data) {
            when (action) {
                DialogueAction.HotWordDetected -> MqttService.sessionStarted(sessionId!!)
                DialogueAction.SessionStarted -> TODO()
                DialogueAction.ToggleHotWordOn -> TODO()
                DialogueAction.ToggleHotWordOff -> TODO()
                DialogueAction.StartListening -> TODO()
                DialogueAction.StopListening -> TODO()
                DialogueAction.SessionEnded -> TODO()
                else -> {}
            }
        }
    }

}