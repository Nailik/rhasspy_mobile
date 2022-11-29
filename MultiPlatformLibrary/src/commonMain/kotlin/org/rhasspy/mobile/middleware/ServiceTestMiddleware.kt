package org.rhasspy.mobile.middleware

import kotlinx.coroutines.launch
import org.rhasspy.mobile.middleware.action.MqttAction

class ServiceTestMiddleware : IServiceMiddleware() {


    override fun mqttAction(event: MqttAction) {
        coroutineScope.launch {
            when (event) {
                is MqttAction.AsrError -> {
                    if (event.sessionId == sessionId) {
                        rhasspyActionsService.endSpeechToText(event.sessionId ?: "")
                    }
                }
                 is MqttAction.AsrTextCaptured -> {
                    if (event.sessionId == sessionId) {
                        rhasspyActionsService.endSpeechToText(event.sessionId ?: "")
                    }
                }
                else -> {}
            }
        }
    }

}