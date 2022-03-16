package org.rhasspy.mobile.services.dialogue

import org.rhasspy.mobile.services.MqttService
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object DialogueManagementMQTT : IDialogueManagement() {

    override suspend fun doAction(inputAction: DialogueInputAction) {
        when (inputAction) {
            DialogueInputAction.HotWordDetected -> {
                //send that wake word was toggled
                MqttService.toggleOnWakeWord()
            }
            DialogueInputAction.StartSession -> {

            }
            DialogueInputAction.SessionStarted -> TODO()
            DialogueInputAction.ToggleHotWordOn -> TODO()
            DialogueInputAction.ToggleHotWordOff -> TODO()
            DialogueInputAction.StartListening -> TODO()
            DialogueInputAction.StopListening -> TODO()
            DialogueInputAction.SessionEnded -> TODO()
        }
    }



}

/*
  ServiceInterface.sessionId = uuid4().also {
                    if (ConfigurationSettings.isMQTTEnabled.data) {
                        MqttService.toggleOnWakeWord() //dialogue management mqtt
                        MqttService.sessionStarted(it)
                    } else {
                        //toggle off wakeword internal
                        ServiceInterface.setListenForWake(false) //TODO external made off
                    }
                }
 */

//TODo wakeword and speech to text mqtt should only be used with dialogue manager set to mqtt
//TODO dialogue management disabled -> when starting and stopping listening with http calls (only then no reactin to mqtt messages)