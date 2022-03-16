package org.rhasspy.mobile.services.dialogue

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

object DialogueManagementLocal : IDialogueManagement() {

    override suspend fun doAction(inputAction: DialogueInputAction) {
        when (inputAction) {
            /** Dialogue Manager */
            //Start a new session
            DialogueInputAction.StartSession -> {
                //create session id
                sessionId = uuid4().toString()
                //start listening
                doAction(DialogueInputAction.StartListening)

                //New session has started
                publishActionOnMQTT(DialogueOutputAction.SessionStarted)
            }
            //End an existing session
            DialogueInputAction.EndSession -> {
                //reset session id
                sessionId = null
                //stop listening
                doAction(DialogueInputAction.StopListening)
            }
            /** Wake Word Detection */
            //Enables wake word detection
            DialogueInputAction.ToggleHotWordOn -> ServiceInterface.setWakeWordEnabled(true)
            //Disables wake word detection
            DialogueInputAction.ToggleHotWordOff -> ServiceInterface.setWakeWordEnabled(false)
            /** Speech to Text */
            //shows indication and starts recording
            DialogueInputAction.StartListening -> ServiceInterface.startRecording()
            //hides indication and stops recording
            DialogueInputAction.StopListening -> ServiceInterface.stopRecording()
            /** Audio Output */
            DialogueInputAction.ToggleAudioOutputOff -> ServiceInterface.setAudioOutputEnabled(false)
            DialogueInputAction.ToggleAudioOutputOn -> ServiceInterface.setAudioOutputEnabled(true)
        }
    }

    override suspend fun wakeWordDetected() {
        //start Session
        doAction(DialogueInputAction.StartSession)
    }


    /**
     * send that some action was done to mqtt broker
     * only if mqtt is enabled
     */
    private fun publishActionOnMQTT(outputAction: DialogueOutputAction) {
        if (ConfigurationSettings.isMQTTEnabled.data) {
            when (outputAction) {
                DialogueOutputAction.SessionStarted -> TODO()
                DialogueOutputAction.SessionEnded -> TODO()
                DialogueOutputAction.AudioFrame -> TODO()
                DialogueOutputAction.HotWordDetected -> TODO()
                DialogueOutputAction.HotWordError -> TODO()
                DialogueOutputAction.AudioCaptured -> TODO()
                DialogueOutputAction.Say -> TODO()
                DialogueOutputAction.PlayFinished -> TODO()
            }
        }
    }

}