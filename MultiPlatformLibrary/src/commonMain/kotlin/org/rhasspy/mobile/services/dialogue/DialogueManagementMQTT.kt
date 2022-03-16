package org.rhasspy.mobile.services.dialogue

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.services.DialogueAction
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object DialogueManagementMQTT : IDialogueManagement() {

    override suspend fun doAction(action: DialogueAction) {
        when (action) {
            DialogueAction.HotWordDetected -> {
                //send that wake word was toggled
                MqttService.toggleOnWakeWord()
            }
            DialogueAction.StartSession -> {

            }
            DialogueAction.SessionStarted -> TODO()
            DialogueAction.ToggleHotWordOn -> TODO()
            DialogueAction.ToggleHotWordOff -> TODO()
            DialogueAction.StartListening -> TODO()
            DialogueAction.StopListening -> TODO()
            DialogueAction.SessionEnded -> TODO()
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